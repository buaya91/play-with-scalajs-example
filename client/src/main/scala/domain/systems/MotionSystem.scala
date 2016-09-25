package domain.systems

import domain.{GameWorld, components}
import domain.components._

class MotionSystem(x: Int, y: Int) extends GameSystem {
  type EntityId = String

  private def positiveModulo(a: Int, n: Int): Int = {
    // to get positive mod
    // we compute remainder, and add back the n
    // to prevent result bigger than n which violate the rules
    // we take the remainder again
    ((a % n) + n) % n
  }

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

            world.areaComponents.put(id, newHead +: newTail)

          case _ => // do nothing
        }
    }
  }
}
