package domain.systems

import domain.{GameWorld, components}
import domain.components._
import utils.Utility._

class MotionSystem(x: Double, y: Double) extends GameSystem {
  type EntityId = String

  def process(world: GameWorld): Unit = {
    world.areaComponents.foreach {
      case (id, body) =>
        val speedOpt = world.speedComponents.get(id)
        val dirOpt = world.directionComponents.get(id)

        (speedOpt, dirOpt) match {
          case (Some(spd), Some(dir)) =>
            val step: Double = spd.distancePerSecond.toDouble / world.frameRate.toDouble

            val diffBetweenEachElement = for {
              i <- 1 until body.size
            } yield {
              val a = body(i - 1)
              val b = body(i)

              a - b
            }

            val oldHead = body.head

            val newHead = dir match {
              case Up => oldHead.copy(y = positiveModulo(oldHead.y - step, y))
              case Down => oldHead.copy(y = positiveModulo(oldHead.y + step, y))
              case components.Right => oldHead.copy(x = positiveModulo(oldHead.x + step, x))
              case components.Left => oldHead.copy(x = positiveModulo(oldHead.x - step, x))
            }

            val movedBody = diffBetweenEachElement.foldLeft(Seq(newHead)) {
              case (newBody, diff) =>
                val last = newBody.last
                val next = last - diff
                newBody :+ next
            }

            world.add(id, movedBody)

          case _ => // do nothing
        }
    }
  }
}
