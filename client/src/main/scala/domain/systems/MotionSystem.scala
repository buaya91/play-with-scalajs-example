package domain.systems

import domain.{GameWorld, components}
import domain.components._
import utils.Utility._

class MotionSystem(x: Double, y: Double) extends GameSystem {
  type EntityId = String

  private def minimizeDiff(diff: Position): Position = {
    // handle change at edge of world
    // as system move in small-steps, we need to reduce
    // steps which are big value into small value which mean the same thing in modulo world
    // eg.
    // -99 -> 1
    // 99 -> -1

    diff match {
      case Position(dx, _) if Math.abs(dx) > x / 2 =>

        val plusN = dx + x
        val minusN = dx - x

        val newX =
          if (Math.abs(plusN) > Math.abs(minusN))
            minusN
          else
            plusN

        diff.copy(x = newX)

      case Position(_, dy) if Math.abs(dy) > y / 2 =>

        val plusN = dy + x
        val minusN = dy - x

        val newY =
          if (Math.abs(plusN) > Math.abs(minusN))
            minusN
          else
            plusN

        diff.copy(y = newY)

      case _ => diff
    }
  }

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

              minimizeDiff(a - b)
            }

            val oldHead = body.head

            val newHead = dir match {
              case Up => oldHead.copy(y = positiveModulo(oldHead.y - step, y))
              case Down => oldHead.copy(y = positiveModulo(oldHead.y + step, y))
              case components.Right => oldHead.copy(x = positiveModulo(oldHead.x + step, x))
              case components.Left => oldHead.copy(x = positiveModulo(oldHead.x - step, x))
            }

            val newBody = diffBetweenEachElement.zip(body.tail).map {
              case (diff, pos) =>
//                println(s"P: $pos")
//                println(s"D: $diff")
//                println(s"New: ${pos + (diff * step)}")
                val ele = pos + (diff * step)

                ele.copy(x = positiveModulo(ele.x, x), y = positiveModulo(ele.y, y))
            }

            world.add(id, newHead +: newBody)

          case _ => // do nothing
        }
    }
  }
}
