package infrastructure

import api.SnakeApi
import domain.GameWorld
import domain.components.{Direction, Position, Speed}
import domain.systems.{CollisionSystem, IntentSystem, MotionSystem}
import org.scalajs.dom
import scala.scalajs.js.timers._
import scala.concurrent.duration._
import scala.collection.mutable
import scala.util.Random

import domain.components._

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

  override def addNewSnake(newSnakeId: String): Unit = {
    val (x, y) = (Random.nextInt() % gameX, Random.nextInt() % gameY)
    val direction = Right
    val snakeArea = SnakeGameImpl.build(newSnakeId, Position(x, y), direction)

    world.add(newSnakeId, snakeArea)
    world.add(newSnakeId, true)
    world.add(newSnakeId, direction)
    world.add(newSnakeId, Speed(1))
  }

  setInterval(300 millis) {
    world.process()
  }
}

object SnakeGameImpl {
  def build(id: String, head: Position, direction: Direction): Seq[Position] = {
    def incrementFunc(n: Int): Position = direction match {
      case Up => head.copy(y = head.y + n)
      case Down => head.copy(y = head.y - n)
      case Right => head.copy(x = head.x - n)
      case Left => head.copy(x = head.x + n)
    }

    val pt = for {
      i <- 1 to 5
    } yield incrementFunc(i)

    head +: pt
  }
}