package infrastructure

import domain.{GameRepo, GameWorld}
import domain.components._
import domain.systems.{CollisionSystem, CommunicationSystem, IntentSystem, MotionSystem}
import monix.execution.Scheduler.Implicits.global
import org.scalajs.dom

import scala.scalajs.js.timers._
import scala.concurrent.duration._
import configs.Config._
import monix.reactive.Observable

// the actual game instance, one per browser session
class MultiplayerSnakeGameImpl(
                                canvasCtx: dom.CanvasRenderingContext2D,
                                val world: GameWorld = new GameWorld()
                              ) extends MultiplayerSnakeApi {

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

  val updateInterval = (1 / world.frameRate) * 1000
  setInterval(updateInterval millis) {
    world.process()
  }

  override def onServerEvents(eventStream: Observable[GlobalEvent]): Unit = {
    eventStream.foreach {
      case SnakeAdded(id, body, dir, spd) =>
        world.add(id, body)
        world.add(id, dir)
        world.add(id, spd)
        world.add(id, true)

      case AppleAdded(id, p) => world.add(id, Seq(p))

      case DirectionChanged(id, newDir) => this.changeDir(id, newDir)

      case EntityRemoved(id) => this.world.remove(id)
    }
  }
}
