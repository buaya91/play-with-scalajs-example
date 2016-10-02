package domain.systems

import domain.GameWorld
import domain.components._

class CollisionSystem extends GameSystem {

  def overlapped(area: Seq[Position], position: Position): Boolean = area.contains(position)

  def bumpToOthers(area: Seq[Position], othersBody: Seq[Position]): Boolean = {
    othersBody.contains(area.head)
  }

  def bumpedToSelf(area: Seq[Position]): Boolean = area.distinct.size != area.size

  def increaseLength(snake: Seq[Position], direction: Direction): Seq[Position] = {
    val last = snake.last
    val newLast = direction match {
      case Up => last.copy(y = last.y + 1)
      case Down => last.copy(y = last.y - 1)
      case Right => last.copy(x = last.x - 1)
      case Left => last.copy(x = last.x + 1)
    }

    snake :+ newLast
  }

  def process(world: GameWorld) = {
    val allCollidable = world.areaComponents

    for {
      (id1, c1) <- allCollidable
      (id2, c2) <- allCollidable
    } yield {

      // if snake bump to self, kill it
      if (bumpedToSelf(c1))
        world.remove(id1)

      if (id1 != id2 && bumpToOthers(c1, c2)) {
        val c1IsSnake = world.isSnakeComponents.getOrElse(id1, false)
        val c2IsSnake = world.isSnakeComponents.getOrElse(id2, false)
        val c1Direction = world.directionComponents.get(id1)

        (c1IsSnake, c2IsSnake, c1Direction) match {

          // if snake bump to another snake, kill it
          case (true, true, _) => world.remove(id1)

          // if snake bump to apple, remove apple and add length to snake
          case (true, false, Some(dir)) =>
            val newSnake = increaseLength(c1, dir)
            world.areaComponents.update(id1, newSnake)

          case _ => // do nothing
        }
      }
    }
  }
}
