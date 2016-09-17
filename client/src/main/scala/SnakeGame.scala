import infrastructure.{CanvasRenderer, InputControl}
import monix.reactive.Observable
import monix.execution.Scheduler.Implicits.global

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, MouseEvent}
import org.scalajs.dom.raw.HTMLCanvasElement

import scala.scalajs.js.annotation.JSExport
import scala.concurrent.duration._

@JSExport
object SnakeGame extends js.JSApp {
  import api.SnakeApiModule

  @scala.scalajs.js.annotation.JSExport
  override def main(): Unit = {
    val h = dom.window.innerHeight
    val w = dom.window.innerWidth
    val canvas = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    canvas.height = h.toInt
    canvas.width = w.toInt

    val id = "user"
    val module = new SnakeApiModule(id, w.toInt / 10, h.toInt / 10)

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    CanvasRenderer.render(ctx, module.world)

    canvas.focus()
    val inputStream = InputControl.captureEvents(canvas)

    Observable
      .interval(300 millis)
      .foreach(l => {
        module.step()
        CanvasRenderer.render(ctx, module.world)
      })

    inputStream.foreach(kv => {
      println(kv.key)
      kv.keyCode match {
        case 37 => module.changeDir(id, domain.Left)
        case 38 => module.changeDir(id, domain.Up)
        case 39 => module.changeDir(id, domain.Right)
        case 40 => module.changeDir(id, domain.Down)
      }
    })
  }
}
