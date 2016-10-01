package domain.systems
import domain.{GameWorld, components}
import domain.components.{ChangeDirection, Down, SpeedUp, Up}

class IntentSystem extends GameSystem {
  override def process(world: GameWorld): Unit = {
    val intents = world.intentComponents
    intents.foreach {
      case (id, ChangeDirection(dir)) =>

        val targetIsSnake = world.isSnakeComponents.getOrElse(id, false)

        if (targetIsSnake) {

          val oldDir = world.directionComponents.getOrElse(id, throw new IndexOutOfBoundsException(s"$id does not have direction"))

          // do not change if direction is opposite
          (oldDir, dir) match {
            case (Up(), Down()) =>
            case (Down(), Up()) =>
            case (components.Left(), components.Right()) =>
            case (components.Right(), components.Left()) =>
            case _ => world.directionComponents.put(id, dir)
          }
        }

      case (id, SpeedUp) =>
        val targetIsSnake = world.isSnakeComponents.getOrElse(id, false)
        ???
    }
  }
}
