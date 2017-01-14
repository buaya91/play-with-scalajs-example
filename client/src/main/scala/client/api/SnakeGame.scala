package client.api

import client.domain._
import monix.execution.Ack.Continue
import monix.execution.Scheduler
import shared.protocol.{AssignedID, GameState}

import scala.concurrent.duration._
import scala.language.postfixOps

class SnakeGame(authorityState: AuthorityState,
                renderer: GameRenderer,
                predictor: Predictor,
                inputControl: InputControl,
                scoreRenderer: ScoreRenderer) {

  def startGame()(implicit scheduler: Scheduler): Unit = {

    // multicast streams
    val responses = authorityState.stream().publish

    val gameStateStream = responses.collect { case x: GameState => x }.publish

    val scoreStream = gameStateStream
      .sample(1.5 seconds)
      .map(state => {
        state.snakes.map(s => (s.name -> s.body.size)).toMap
      })
      .distinct

    val sequencedInput = inputControl
      .captureInputs()
      .withLatestFrom(gameStateStream) {
        case (noToReq, state) => noToReq.apply(state.seqNo)
      }
      .publish

    // single emission task
    val assignedID = responses.collect { case a: AssignedID => a.id }.headF.publish

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

    scoreStream.subscribe(score => {
      scoreRenderer.render(score)
      Continue
    })

    responses.connect()
    assignedID.connect()
    gameStateStream.connect()
    sequencedInput.connect()
  }
}
