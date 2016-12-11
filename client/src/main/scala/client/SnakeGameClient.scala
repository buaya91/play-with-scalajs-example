package client

import client.JSFacade.JQueryStatic
import client.debug.DebugPanel
import client.gameplay.infrastructure._
import org.scalajs.dom._

import scala.scalajs.js._
import monix.execution.Scheduler.Implicits.global
import monix.reactive.Consumer
import org.scalajs.dom.raw._
import shared.protocol.{AssignedID, GameState, JoinGame}

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

  @annotation.JSExport
  override def main(): Unit = {
    val serverSrc = ServerSource.src().publish

    val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]
    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    val rendererConsumer = Consumer.foreach[(String, GameState)] {
      case (id, state) =>
        CanvasRenderer.render(ctx, state, id)
    }

    // todo: might not be true later
    serverSrc
      .collect { case a: AssignedID => a }
      .firstL
      .runAsync(_ => JQueryStatic("#username-modal").modal("hide"))

    serverSrc
      .scan(("", GameState.init)) {
        case (pair, AssignedID(id)) => (id, pair._2)
        case (pair, x: GameState) => (pair._1, x)
      }
      .consumeWith(rendererConsumer)
      .runAsync

    serverSrc.connect()

    InputControl
      .captureEvents(document.asInstanceOf[HTMLElement])
      .foreach(ServerSource.send)

    initModal()
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

    gameState
      .flatMap(state => assignedID.map(a => (a.id, state)))
      .foreach {
        case (id, state) => DebugRenderer.render(ctx, state, id)
      }

    serverSrc.subscribe()
    addDebugPanel()
  }

  def addDebugPanel(): Unit = {
    document.body.appendChild(DebugPanel(DebugSource.send).render)
  }
}
