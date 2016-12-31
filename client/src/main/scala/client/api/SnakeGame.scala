package client.api

import client.domain.{AuthorityState, InputControl, Predictor, Renderer}
import monix.execution.Scheduler
import monix.reactive.Consumer
import shared.protocol.{AssignedID, GameState, SequencedGameRequest}

class SnakeGame(authorityState: AuthorityState, renderer: Renderer, predictor: Predictor, inputControl: InputControl) {

  private val rendererConsumer = Consumer.foreach[(String, GameState)] {
    case (id, state) =>
      renderer.render(state, id)
  }

  private val requestConsumer = Consumer.foreach[SequencedGameRequest](r => authorityState.request(r))

  def startGame(onAssignedID: String => _)(implicit scheduler: Scheduler): Unit = {

    // stream primitives
    val responses       = authorityState.stream
    val gameStateStream = responses.collect { case x: GameState => x }
    val assignedID      = responses.collect { case a: AssignedID => a }.firstL
    val inputs = inputControl.captureEventsKeyCode.withLatestFrom(gameStateStream) {
      case (noToReq, state) => noToReq.apply(state.seqNo)
    }
    val predictions =
      responses.collect { case a: AssignedID => a }.flatMap(id =>
        predictor.predictions(id.id, gameStateStream, inputs))

    // side-effect tasks
    val assignedIDCallBackTask = assignedID.map { case AssignedID(id) => onAssignedID(id) }

    val renderTask = predictions
      .mapAsync(state => assignedID.map { case AssignedID(id) => (id, state) })
      .consumeWith(rendererConsumer)
    val sendRequestTask = inputs.consumeWith(requestConsumer)

    assignedIDCallBackTask.runAsync
    renderTask.runAsync
    sendRequestTask.runAsync
  }
}
