import domain.components.{Down, EntityRemoved, Left, Right, Up}
import infrastructure.{FirebaseGameRepo, InputControl, MultiplayerSnakeGameImpl}
import org.scalajs.dom
import org.scalajs.dom.BeforeUnloadEvent
import org.scalajs.dom.raw.{CanvasRenderingContext2D, HTMLCanvasElement}
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.concurrent.Promise
import scala.util.Success

import monix.execution.Scheduler.Implicits.global

@JSExport
object SnakeGame extends js.JSApp {

  @JSExport
  override def main(): Unit = {

    val namePromise: Promise[String] = Promise()

    jQuery("#submit-name").on("click", (ev: JQueryEventObject) => {
      val name = jQuery("#name-prompt input").`val`().toString
      namePromise.complete(Success(name))
      jQuery("#name-prompt").addClass("hidden")
    })

    dom.window.onbeforeunload = (ev: BeforeUnloadEvent) => {
      if (uidF.isCompleted) {
        uidF.map(uid => {
          FirebaseGameRepo.broadcastEvent(EntityRemoved(uid))
        })
      }
    }

    lazy val uidF = namePromise.future

    val canvas = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    canvas.height = 600
    canvas.width = 600

    val canvasCtx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    FirebaseGameRepo.init().map(w => {
      val gameImpl = new MultiplayerSnakeGameImpl(canvasCtx, w)
      uidF.map(uid => {
        gameImpl.addNewSnake(uid)

        InputControl.captureEvents(canvasCtx.canvas).foreach(kv =>
          kv.keyCode match {
            case 37 => gameImpl.changeDir(uid, Left)
            case 38 => gameImpl.changeDir(uid, Up)
            case 39 => gameImpl.changeDir(uid, Right)
            case 40 => gameImpl.changeDir(uid, Down)
            case _  => // ignore others
          })
      })
    })


    /**
      * 1. Prompt user for id
      * 2. Check uniqueness, if unique, create user, else go to 1.
      * 3. Get game instance from backend, create and sync back if none, there's a chance of conflict, ignore ATM
      * 4. Subscribe to game events
      * 5. Publish events
      */

  }
}
