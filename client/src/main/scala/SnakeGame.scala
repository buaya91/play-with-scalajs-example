import infrastructure.{FirebaseGameRepo, MultiplayerSnakeGameImpl}
import org.scalajs.dom
import org.scalajs.dom.raw.{CanvasRenderingContext2D, HTMLCanvasElement}
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.Success

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

    val uidF = namePromise.future

    val canvas = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    canvas.height = 600
    canvas.width = 600

    val canvasCtx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    val game = for {
      worldFromServer <- FirebaseGameRepo.init()
      uid             <- uidF
    } yield {
      val game = new MultiplayerSnakeGameImpl(uid, canvasCtx, worldFromServer)
      game.addNewSnake(uid)
    }

    /**
      * 1. Prompt user for id
      * 2. Check uniqueness, if unique, create user, else go to 1.
      * 3. Get game instance from backend, create and sync back if none, there's a chance of conflict, ignore ATM
      * 4. Subscribe to game events
      * 5. Publish events
      */

  }
}
