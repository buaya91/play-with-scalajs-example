package api

import domain.{Direction, Down, Left, Position, Right, Snake, Up}

trait SnakeApi {
  def build(id: String, head: Position, direction: Direction): Snake = {
    def incrementFunc(n: Int): Position = direction match {
      case Up => head.copy(y = head.y + n)
      case Down => head.copy(y = head.y - n)
      case Right => head.copy(x = head.x - n)
      case Left => head.copy(x = head.x + n)
    }

    val pt = for {
      i <- 1 to 5
    } yield incrementFunc(i)

    Snake(id, head +: pt, direction)
  }
}

object SnakeApiModule extends SnakeApi