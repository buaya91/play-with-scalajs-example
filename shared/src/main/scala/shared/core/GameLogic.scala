package shared.core

import shared.model._
import shared._
import shared.physics.{AABB, PhysicsFormula, Vec2}
import shared.protocol._

//TODO: figure out how to compile loop while maintain readability
object GameLogic {

  private def removeDeadSnakes(state: GameState): GameState = {
    val survivedSnakes = state.snakes.filterNot(snake => {
      val others = state.snakes
      val targetHead = snake.body.head

      others.exists(s => s != snake && s.body.tail.exists(targetHead.collided))
    })

    state.copy(snakes = survivedSnakes)
  }

  private def removeEatenApple(state: GameState): GameState = {
    val snakeApple = for {
      s <- state.snakes
      a <- state.apples
      if s.body.exists(_.collided(a.position))
    } yield {
      (s, a)
    }

    val (snakesAteApple, appleEaten) = snakeApple.unzip

    val updatedSnake = state.snakes.map {
      case s if snakesAteApple.exists(_.id == s.id) =>
        val last2 = s.body.takeRight(2)
        val secondLastCenter = last2(1).center
        val diff = secondLastCenter - last2.head.center
        val appended = s.body :+ AABB(secondLastCenter + diff, last2(1).halfExtents)
        s.copy(body = appended)
      case x => x
    }

    val appleNotEaten = state.apples.filterNot(a => appleEaten.contains(a))
    GameState(updatedSnake, appleNotEaten)
  }

  private def applyInput(state: GameState, inputs: Seq[IdentifiedGameInput]): GameState = {
    val updatedSnakes = inputs.foldLeft(state.snakes) {
      case (s, IdentifiedGameInput(id, ChangeDirection(dir))) =>
        s.map {
          case targeted if targeted.id == id =>
            targeted.copy(direction = dir)
          case other => other
        }

      case (s, IdentifiedGameInput(id, SpeedUp)) =>
        ???

      case (s, IdentifiedGameInput(id, JoinGame(name))) =>
        val emptyBlock = PhysicsFormula.findContiguousBlock(shared.terrainX, shared.terrainY, snakeBodyInitLength)
        val newSnake = Snake(id, name, emptyBlock, Up, defaultSpeed)
        s :+ newSnake

      case (s, IdentifiedGameInput(id, LeaveGame)) =>
        s.filter(_.id != id)
    }

    state.copy(snakes = updatedSnakes)
  }

  private def roundingBack(position: Vec2, boundary: (Double, Double)): Vec2 = (position, boundary) match {
    case (Vec2(x, y), (xMax, yMax)) =>
      val adjustedX = (if (x < 0) x + xMax else x) % xMax
      val adjustedY = (if (y < 0) y + yMax else y) % yMax
      Vec2(adjustedX, adjustedY)
  }

  private def applyMovement(state: GameState): GameState = {
    val moved = state.snakes.map(snake => {
      val diffBetweenElements =
        for {
          i <- 1 until snake.body.size
        } yield {
          val front: Vec2 = snake.body(i - 1).center
          val back: Vec2 = snake.body(i).center

          val diff = {
            (front - back).map(v =>
              Math.abs(v) match {
                case abs if abs > terrainX / 2 =>
                  abs / -v
                case x => v
            })
          }

          assert(Math.abs(diff.magnitude) <= 4, s"Distance between snake body is too long: $diff")

          diff
        }

      val movedHead = {
        val moveStep = unitPerDirection(snake.direction) * snake.distancePerStep
        val h = snake.body.head
        h.copy(center = h.center + moveStep)
      }

      val movedTail = snake.body.tail.zip(diffBetweenElements).map {
        case (ele, vec) => ele.copy(ele.center + (vec * snake.distancePerStep))
      }

      val movedBody: Seq[AABB] = (movedHead +: movedTail) map {
        case aabb @ AABB(center, _) => aabb.copy(center = roundingBack(center, (terrainX, terrainY)))
      }

      snake.copy(body = movedBody)
    })
    state.copy(snakes = moved)
  }

  private def replenishApple(state: GameState): GameState = {
    val diff = state.snakes.size - state.apples.size

    val apples = for {
      _ <- 0 to diff
    } yield {
      Apple(PhysicsFormula.findContiguousBlock(shared.terrainX, shared.terrainY, 1).head)
    }

    state.copy(apples = state.apples ++ apples.toSet)
  }

  def step(state: GameState, inputs: Seq[IdentifiedGameInput]): GameState = {
    val allSteps =
      (removeDeadSnakes _)
        .andThen(removeEatenApple)
        .andThen(s => applyInput(s, inputs))
        .andThen(applyMovement)
        .andThen(replenishApple)

    allSteps(state)
  }
}
