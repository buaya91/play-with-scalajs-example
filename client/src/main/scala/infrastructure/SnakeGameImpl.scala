package infrastructure

import api.SnakeApi
import domain.{GameRepo, GameWorld}
import domain.components._
import domain.systems.{CollisionSystem, CommunicationSystem, IntentSystem, MotionSystem}
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom

import scala.scalajs.js.timers._
import scala.concurrent.duration._
import configs.Config._

// the actual game instance, one per browser session
class SnakeGameImpl(
                     canvasCtx: dom.CanvasRenderingContext2D,
                     val world: GameWorld = new GameWorld()
                   ) extends SnakeApi {

  val intentSystem = new IntentSystem()
  val collisionSystem = new CollisionSystem()
  val motionSystem = new MotionSystem(gameX, gameY)
  val renderSystem = new CanvasRenderSystem(canvasCtx)
  val communicationSystem = new CommunicationSystem {
    override val gameRepo: GameRepo = FirebaseGameRepo
  }

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
