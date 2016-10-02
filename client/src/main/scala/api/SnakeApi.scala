package api

import domain._
import domain.components.{Direction, Down, Left, Position, Right, Speed, Up}
import scala.util.Random
import configs.Config._

import utils.Utility._

trait SnakeApi {
  val world: GameWorld

  // to add new snake
  def addNewSnake(newSnakeId: String): Unit = {
    val (x, y) = (positiveModulo(Random.nextInt(), gameX), positiveModulo(Random.nextInt(), gameY))
    val direction = Right
    val snakeArea = SnakeApi.build(Position(x, y), direction)

    world.add(newSnakeId, snakeArea)
    world.add(newSnakeId, true)
    world.add(newSnakeId, direction)
    world.add(newSnakeId, Speed(2))
  }

  def changeDir(id: String, dir: Direction): Unit
  def speedUp(id: String): Unit
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