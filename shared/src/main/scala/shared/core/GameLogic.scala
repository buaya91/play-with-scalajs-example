package shared.core

import shared.model._
import shared._
import shared.physics.{AABB, PhysicsFormula, Vec2}
import shared.protocol.{ChangeDirection, JoinGame}

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
        i <- 1 until snake.body.size
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

  def applyInput(state: GameState, inputs: Seq[IdentifiedGameInput]): GameState = {
    val updatedSnakes = inputs.foldLeft(state.snakes) {
      case (s, IdentifiedGameInput(id, ChangeDirection(dir))) =>
        s.map {
          case targeted if targeted.id == id =>
            targeted.copy(direction = dir)
          case other => other
        }

      case (s, IdentifiedGameInput(id, JoinGame)) =>
        val newSnake = Snake(id,
          PhysicsFormula.findContiguousBlock(shared.terrainX, shared.terrainX),
          Up
        )
        s :+ newSnake
    }

    state.copy(snakes = updatedSnakes)
  }

  def step(state: GameState, inputs: Seq[IdentifiedGameInput]): GameState = {

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
