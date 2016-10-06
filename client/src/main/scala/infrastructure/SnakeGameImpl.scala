package infrastructure

import api.SnakeApi
import domain.GameWorld
import domain.components._
import domain.systems.{CollisionSystem, IntentSystem, MotionSystem}
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom
import scala.scalajs.js.timers._
import scala.concurrent.duration._
import scala.collection.mutable

import configs.Config._

class SnakeGameImpl(
                     clientId: String,
                     canvasCtx: dom.CanvasRenderingContext2D,
                     val areaComponents: mutable.Map[String, Seq[Position]] = mutable.HashMap(),
                     val isSnakeComponents: mutable.Map[String, Boolean] = mutable.HashMap(),
                     val speedComponents: mutable.Map[String, Speed] = mutable.HashMap(),
                     val directionComponents: mutable.Map[String, Direction] = mutable.HashMap()
                   ) extends SnakeApi {

  override val world: GameWorld = new GameWorld(areaComponents, isSnakeComponents, speedComponents, directionComponents)

  override def changeDir(id: String, dir: Direction): Unit = {
    world.intentComponents.update(id, ChangeDirection(dir))
  }

  override def speedUp(id: String): Unit = ???

  val intentSystem = new IntentSystem()
  val collisionSystem = new CollisionSystem()
  val motionSystem = new MotionSystem(gameX, gameY)
  val renderSystem = new CanvasRenderSystem(canvasCtx)
  val communicationSystem = new FirebaseCommunicationSystem()

  world.addSystem(intentSystem)
  world.addSystem(motionSystem)
  world.addSystem(collisionSystem)
  world.addSystem(renderSystem)
  world.addSystem(communicationSystem)

  InputControl.captureEvents(canvasCtx.canvas).foreach(kv =>
    kv.keyCode match {
      case 37 => this.changeDir(clientId, Left)
      case 38 => this.changeDir(clientId, Up)
      case 39 => this.changeDir(clientId, Right)
      case 40 => this.changeDir(clientId, Down)
      case _  => // ignore others
    })

  val updateInterval = (1 / world.frameRate) * 1000
  setInterval(updateInterval millis) {
    world.process()
  }
}
