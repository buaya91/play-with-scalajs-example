package client

import client.infrastructure.{CanvasRenderer, SourceForTest}
import org.scalajs.dom._

import scala.scalajs.js._
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._

object SnakeGameClient extends JSApp {

  @annotation.JSExport
  override def main(): Unit = {
    val stateSrc = SourceForTest.subscribe()
    val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    stateSrc.sample(1 second).foreach(state => CanvasRenderer.render(ctx, state))

    stateSrc.subscribe()
  }
}
