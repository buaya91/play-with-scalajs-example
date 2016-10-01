import firebase.{DataSnapshot, Firebase}
import infrastructure.SnakeGameImpl
import org.scalajs.dom
import org.scalajs.dom.raw.{CanvasRenderingContext2D, HTMLCanvasElement}
import prickle._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import domain.components.Direction._
import domain.components._

import scala.scalajs.js.JSON
import scala.util.Failure

@JSExport
object SnakeGame extends js.JSApp {

  @JSExport
  override def main(): Unit = {
    val uid = "TestUser"            // todo: create a modal to prompt user

    val canvas = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]

    canvas.height = 600
    canvas.width = 600

    implicit val customConfig = JsConfig("xx", false)    // todo: test if we could remove it

    val dbRoot = Firebase.database().ref()
    dbRoot.once("value", (db: DataSnapshot) => {
      if (db.`val`() == null) {
        val game = new SnakeGameImpl(canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D])
        game.addNewSnake(uid)

        val gameDb = dbRoot.child("0")

        game.world.areaComponents.foreach {
          case (id, area) =>
            val jsonArea = Pickle.intoString(area)
            gameDb.child(s"area/$id").set(JSON.parse(jsonArea))
        }

        game.world.directionComponents.foreach {
          case (id, dir) =>
            val jsonArea = Pickle.intoString(dir)
            println(jsonArea)
            println(JSON.parse(jsonArea))
            gameDb.child(s"dir/$id").set(JSON.parse(jsonArea))
        }

        game.world.isSnakeComponents.foreach {
          case (id, isSnake) =>
            val jsonArea = Pickle.intoString(isSnake)
            gameDb.child(s"isSnake/$id").set(JSON.parse(jsonArea))
        }

        game.world.speedComponents.foreach {
          case (id, speed) =>
            val jsonArea = Pickle.intoString(speed)
            gameDb.child(s"speed/$id").set(JSON.parse(jsonArea))
        }
      }
    })
  }
}
