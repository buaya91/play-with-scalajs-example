package client.api

import client.domain.{AuthorityState, InputControl, Predictor, Renderer}
import monix.execution.Ack.{Continue, Stop}
import monix.execution.Scheduler
import shared.protocol.{AssignedID, GameState}

class SnakeGame(authorityState: AuthorityState, renderer: Renderer, predictor: Predictor, inputControl: InputControl) {

  def startGame(onAssignedID: String => _)(implicit scheduler: Scheduler): Unit = {

    // multicast streams
    val responses = authorityState.stream().publish

    val gameStateStream = responses.collect { case x: GameState => x }.publish

    val sequencedInput = inputControl
      .captureInputs()
      .withLatestFrom(gameStateStream) {
        case (noToReq, state) => noToReq.apply(state.seqNo)
      }
      .publish

    // single emission task
    val assignedID = responses.collect { case a: AssignedID => a.id }.headF.publish

    // side-effect tasks
    val assignedIDCallBackTask = assignedID.subscribe { id =>
      onAssignedID(id)
      Stop
    }

    val renderTask =
      assignedID.flatMap { id =>
        predictor.predictions(id, gameStateStream, sequencedInput).map(s => (id, s))
      }.executeWithFork.subscribe(pair => {
        renderer.render(pair._2, pair._1)
        Continue
      })

    val sendRequestTask = sequencedInput.subscribe(req => {
      authorityState.request(req)
      Continue
    })

    responses.connect()
    assignedID.connect()
    gameStateStream.connect()
    sequencedInput.connect()
//    predictions.connect()
  }
}
