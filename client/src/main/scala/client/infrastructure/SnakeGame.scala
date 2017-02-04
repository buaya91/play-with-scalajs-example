package client.infrastructure

import client.domain._
import client.infrastructure.views.{GameCanvas, Instruction, PlayerStatus, Scoreboard}
import japgolly.scalajs.react.ReactDOM
import monix.execution.Ack.{Continue, Stop}
import monix.execution.Scheduler
import org.scalajs.dom.{document, html}
import shared.protocol.{AssignedID, GameState, SpeedUp}

class SnakeGame(authorityState: AuthorityState, predictor: Predictor, inputControl: InputControl) {

  def startGame()(implicit scheduler: Scheduler): Unit = {

    // multicast streams
    val responses = authorityState.stream().publish

    val gameStateStream = responses.collect { case x: GameState => x }.publish

    val scoreStream = gameStateStream
      .map(state => {
        state.snakes.map(s => s.name -> s.body.size).toMap
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

    val selfSnakeStream = assignedID.flatMap { id =>
      gameStateStream.collect {
        case st: GameState if st.snakes.exists(_.id == id) => st.snakes.find(_.id == id).get
      }
    }

    import SnakeGame._

    ReactDOM.render(Instruction(true), hiddenDiv)

    val firstInput = sequencedInput.filter {
      case x: SpeedUp => true
      case _          => false
    }.take(1).subscribe { _ =>
      ReactDOM.render(Instruction(false), hiddenDiv)
      Stop
    }

    assignedID.flatMap { id =>
      predictor.predictions(id, gameStateStream, sequencedInput).map(s => (id, s))
    }.executeWithFork.subscribe(pair => {
      ReactDOM.render(GameCanvas.apply(pair._2, pair._1), canvasNode)
      Continue
    })

    sequencedInput.subscribe(req => {
      authorityState.request(req)
      Continue
    })

    selfSnakeStream.subscribe(snk => {
      val status = PlayerStatus(snk.name, snk.body.size, snk.energy)
      ReactDOM.render(PlayerStatus(status), statusNode)
      Continue
    })

    scoreStream.subscribe(score => {
      ReactDOM.render(Scoreboard(score.take(10)), scoreboardNode)
      Continue
    })

    responses.connect()
    assignedID.connect()
    gameStateStream.connect()
    sequencedInput.connect()
  }
}

object SnakeGame {
  val canvasNode     = document.getElementById("canvas-container").asInstanceOf[html.Div]
  val scoreboardNode = document.getElementById("scoreboard").asInstanceOf[html.Div]
  val statusNode     = document.getElementById("status-board").asInstanceOf[html.Div]
  val hiddenDiv      = document.getElementById("tips-container").asInstanceOf[html.Div]
}
