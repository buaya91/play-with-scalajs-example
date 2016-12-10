package client

import client.infrastructure._
import org.scalajs.dom._

import scala.scalajs.js._
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom.raw.HTMLElement
import shared.protocol.{DebugNextFrame, GameRequest}

object SnakeGameClient extends JSApp {

  @annotation.JSExport
  override def main(): Unit = {
    val stateSrc = GameStateSource.src()

    val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    stateSrc.foreach(state => CanvasRenderer.render(ctx, state))

    stateSrc.subscribe()

    InputControl.captureEvents(document.asInstanceOf[HTMLElement])
      .foreach(GameStateSource.send)
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
    import scalatags.JsDom.all._

    val debugPanel = div(
      button("Next frame", onclick := {() => DebugSource.send(GameRequest(DebugNextFrame))})
    )

    document.body.appendChild(debugPanel.render)
  }
}
