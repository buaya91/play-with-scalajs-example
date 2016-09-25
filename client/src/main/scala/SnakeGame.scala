import domain.components.{Down, Left, Right, Up}
import infrastructure.{CanvasRenderer, InputControl}
import monix.reactive.Observable
import monix.execution.Scheduler.Implicits.global

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, MouseEvent}
import org.scalajs.dom.raw.HTMLCanvasElement

import scala.scalajs.js.annotation.JSExport
import scala.concurrent.duration._
import scala.scalajs.js.timers._


@JSExport
object SnakeGame extends js.JSApp {

  @scala.scalajs.js.annotation.JSExport
  override def main(): Unit = {

    ???
  }
}
