package domain.systems

import domain.{GameWorld, components}
import domain.components._
import utils.Utility._

class MotionSystem(x: Int, y: Int) extends GameSystem {
  type EntityId = String

  def process(world: GameWorld): Unit = {
    world.areaComponents.foreach {
      case (id, body) =>
        val speedOpt = world.speedComponents.get(id)
        val dirOpt = world.directionComponents.get(id)

        (speedOpt, dirOpt) match {
          case (Some(spd), Some(dir)) =>
            val newTail = body.dropRight(1)
            val oldHead = body.head
            val newHead = dir match {
              case Up => oldHead.copy(y = positiveModulo(oldHead.y - 1, y))
              case Down => oldHead.copy(y = positiveModulo(oldHead.y + 1, y))
              case components.Right => oldHead.copy(x = positiveModulo(oldHead.x + 1, x))
              case components.Left => oldHead.copy(x = positiveModulo(oldHead.x - 1, x))
            }

            world.add(id, newHead +: newTail)

          case _ => // do nothing
        }
    }
  }
}
