package infrastructure

import api.SnakeApi
import domain.GameWorld
import domain.components.{Direction, Position, Speed}
import domain.systems.{CollisionSystem, IntentSystem, MotionSystem}
import org.scalajs.dom
import scala.scalajs.js.timers._
import scala.concurrent.duration._
import scala.collection.mutable

import configs.Config._

class SnakeGameImpl(
                     canvasCtx: dom.CanvasRenderingContext2D,
                     val areaComponents: mutable.Map[String, Seq[Position]] = mutable.HashMap(),
                     val isSnakeComponents: mutable.Map[String, Boolean] = mutable.HashMap(),
                     val speedComponents: mutable.Map[String, Speed] = mutable.HashMap(),
                     val directionComponents: mutable.Map[String, Direction] = mutable.HashMap()
                   ) extends SnakeApi {

  override val world: GameWorld = new GameWorld(areaComponents, isSnakeComponents, speedComponents, directionComponents)

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

  override def changeDir(id: String, dir: Direction): Unit = {
    directionComponents.update(id, dir)
  }

  override def speedUp(id: String): Unit = ???

  val updateInterval = (1 / world.frameRate) * 1000

  setInterval(updateInterval millis) {
    world.process()
  }
}
