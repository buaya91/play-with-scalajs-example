package client

import client.JSFacade.JQueryStatic
import client.debug.DebugPanel
import client.gameplay.infrastructure._
import org.scalajs.dom._

import scala.scalajs.js._
import monix.execution.Scheduler.Implicits.global
import monix.reactive.{Consumer, Observable}
import org.scalajs.dom.raw._
import shared.model
import shared.protocol._

import scala.concurrent.duration._
import scala.language.postfixOps

object SnakeGameClient extends JSApp {

  def onSubmitName(): Unit = {
    val name = document.getElementById("username-input").asInstanceOf[HTMLInputElement].value
    ServerSource.send(JoinGame(name))
    true
  }

  def initModal() = {
    JSFacade
      .JQueryStatic("#username-modal")
      .modal(Dynamic.literal(autofocus = true, onApprove = () => onSubmitName()))
      .modal("show")
  }

  def setCanvasFullScreen(canvas: html.Canvas) = {
    canvas.width = window.innerWidth.toInt
    canvas.height = window.innerHeight.toInt
    canvas.style.height = s"${window.innerHeight}px"
    canvas.style.width = s"${window.innerWidth}px"
  }

  @annotation.JSExport
  override def main(): Unit = {
    val serverSrc = ServerSource.src().publish

    val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]
    val ctx    = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    setCanvasFullScreen(canvas)

    val rendererConsumer = Consumer.foreach[(String, GameState)] {
      case (id, state) =>
        CanvasRenderer.render(ctx, state, id)
    }

    serverSrc.collect { case a: AssignedID => a }.firstL.runAsync(_ => JQueryStatic("#username-modal").modal("hide"))

    serverSrc
      .scan(("", GameState.init)) {
        case (pair, AssignedID(id)) => (id, pair._2)
        case (pair, x: GameState)   => (pair._1, x)
      }
      .consumeWith(rendererConsumer)
      .runAsync

    serverSrc.connect()

    val keyCodePerFrame: Observable[Int] = InputControl
      .captureEventsKeyCode(document.asInstanceOf[HTMLElement])
      .bufferTimedAndCounted(1000 / shared.fps millis, 1)
      .map(_.headOption.getOrElse(0))

    keyCodePerFrame
      .withLatestFrom(serverSrc) {
        case (keyCode, state: GameState) if keyToCmd.isDefinedAt(keyCode) =>
          Some(keyToCmd(keyCode)(state.seqNo))
        case _ => None
      }
      .collect { case Some(x) => x }
      .foreach(ServerSource.send)

    initModal()
  }

  val keyToCmd: PartialFunction[Int, Int => SequencedGameRequest] = {
    case 32 => SpeedUp.apply
    case 37 => ChangeDirection(model.Left, _)
    case 38 => ChangeDirection(model.Up, _)
    case 39 => ChangeDirection(model.Right, _)
    case 40 => ChangeDirection(model.Down, _)
  }

  @annotation.JSExport
  def debugMain(): Unit = {
    val serverSrc = DebugSource.src()

    val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    val gameState = serverSrc.collect {
      case x: GameState => x
    }

    val assignedID = serverSrc.collect {
      case x: AssignedID => x
    }

    gameState.flatMap(state => assignedID.map(a => (a.id, state))).foreach {
      case (id, state) => DebugRenderer.render(ctx, state, id)
    }

    serverSrc.subscribe()
    addDebugPanel()
  }

  def addDebugPanel(): Unit = {
    document.body.appendChild(DebugPanel(DebugSource.send).render)
  }
}
