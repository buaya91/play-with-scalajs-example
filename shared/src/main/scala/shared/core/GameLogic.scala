package shared.core

import shared.model._
import shared._
import shared.physics.{AABB, Vec2}
import shared.protocol.ChangeDirection

/**
  * @author limqingwei
  */
object GameLogic {

  def snakeKilledByOther(snake: Snake, others: Seq[Snake]): Boolean = {
    val targetHead = snake.body.head

    others.exists(s =>
      s != snake && s.body.exists(b => targetHead.collided(b)))
  }

  def move(snake: Snake): Snake = {
    val diffBetweenElements =
      for {
        i <- 1 to snake.body.size
      } yield {
        val front: Vec2 = snake.body(i - 1).center
        val back: Vec2 = snake.body(i).center
        front - back
      }

    val movedHead = {
      val moveStep = unitPerDirection(snake.direction) * snake.distancePerStep
      val h = snake.body.head
      h.copy(center = h.center + moveStep)
    }

    val movedTail = snake.body.tail.zip(diffBetweenElements).map {
      case (ele, vec) => ele.copy(ele.center + (vec * snake.distancePerStep))
    }

    val movedBody: Seq[AABB] = movedHead +: movedTail

    snake.copy(body = movedBody)
  }

  def applyInput(state: GameState, inputs: Set[IdentifiedGameInput]): GameState = {
    val updatedSnakes = inputs.foldLeft(state.snakes) { (s, i) =>
      i match {
        case IdentifiedGameInput(id, ChangeDirection(dir)) =>
          s.map {
            case targeted if targeted.id == id =>
              targeted.copy(direction = dir)
            case other => other
          }
      }
    }
    state.copy(snakes = updatedSnakes)
  }

  def step(state: GameState, inputs: Set[IdentifiedGameInput]): GameState = {

    /**
      * 1. move snakes
      * 2. clear snakes killed
      * 3. check remained snakes if
      */

    val inputApplied = applyInput(state, inputs)
    val movedSnakes = inputApplied.snakes.map(move)
    val survivedSnakes =
      movedSnakes.filterNot(s => snakeKilledByOther(s, movedSnakes))

    inputApplied.copy(snakes = survivedSnakes)
  }
}
