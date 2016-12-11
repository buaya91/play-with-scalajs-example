package client

import client.JSFacade.JQueryStatic
import client.debug.DebugPanel
import client.gameplay.infrastructure._
import org.scalajs.dom._

import scala.scalajs.js._
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom.raw._
import shared.protocol.JoinGame

object SnakeGameClient extends JSApp {

  def onSubmitName(): Unit = {
    val name = document.getElementById("username-input").asInstanceOf[HTMLInputElement].value
    GameStateSource.send(JoinGame(name))
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
    val stateSrc = GameStateSource.src()

    val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    // todo: might not be true later
    stateSrc.firstL.runAsync(_ => JQueryStatic("#username-modal").modal("hide"))
    stateSrc.foreach(state => CanvasRenderer.render(ctx, state))
    stateSrc.subscribe()

    InputControl
      .captureEvents(document.asInstanceOf[HTMLElement])
      .foreach(GameStateSource.send)

    initModal()
  }

  @annotation.JSExport
  def debugMain(): Unit = {
    val stateSrc = DebugSource.src()

    val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    stateSrc.foreach(state => DebugRenderer.render(ctx, state))

    stateSrc.subscribe()
    addDebugPanel()
  }

  def addDebugPanel(): Unit = {
    document.body.appendChild(DebugPanel(DebugSource.send).render)
  }
}
