package api

import domain._
import domain.components._
import configs.Config._

import scala.util.Random

trait SnakeApi {
  val world: GameWorld

  // to add new snake
  def addNewSnake(newSnakeId: String): Unit = {
    val (x, y) = (Random.nextInt(gameX.toInt), Random.nextInt(gameY.toInt))
    val direction = Right
    val spd = Speed(2)
    val snakeArea = SnakeApi.build(Position(x, y), direction)

    world.add(newSnakeId, snakeArea)
    world.add(newSnakeId, true)
    world.add(newSnakeId, direction)
    world.add(newSnakeId, spd)

    val event = SnakeAdded(newSnakeId, snakeArea, direction, spd)
    world.addEvent(event)
  }

  def changeDir(id: String, dir: Direction): Unit = {
    world.intentComponents.update(id, ChangeDirection(dir))
  }

  def speedUp(id: String): Unit = ???
}

object SnakeApi {
  def build(head: Position, direction: Direction): Seq[Position] = {
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