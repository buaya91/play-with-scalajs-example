import infrastructure.CanvasRenderer
import monix.reactive.Observable
import monix.execution.Scheduler.Implicits.global

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.raw.HTMLCanvasElement

import scala.scalajs.js.annotation.JSExport

import scala.concurrent.duration._

@JSExport
object SnakeGame extends js.JSApp {
  import api.SnakeApiModule._

  @scala.scalajs.js.annotation.JSExport
  override def main(): Unit = {
    val h = dom.window.innerHeight
    val w = dom.window.innerWidth
    val canvas = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    canvas.height = h.toInt
    canvas.width = w.toInt

    val game = createGame("user", w.toInt / 10, h.toInt / 10)

    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    CanvasRenderer.render(ctx, game)

    val gameLoop = Observable
      .interval(1 second)
      .scan(game) {
        case (newGame, _) => step(newGame)
      }

    gameLoop.foreach(CanvasRenderer.render(ctx, _))
  }
}
