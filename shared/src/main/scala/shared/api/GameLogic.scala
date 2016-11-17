package shared.api

import shared.model.core.Vec2
import shared.model.{GameState, Position, Snake}

/**
  * @author limqingwei
  */
object GameLogic {
  def snakeKilledBySelf(snake: Snake): Boolean = ???

  def snakeKilledByOther(snake: Snake, others: Seq[Snake]) = ???

  def move(snake: Snake): Snake = {
    val diffBetweenElements =
      for {
        i <- 1 to snake.body.size
      } yield {
        val front = snake.body(i - 1)
        val back = snake.body(i)

      }
  }

  def step(state: GameState, inputs: Set[GameInput]): GameState = {
    /**
      * 1. move snakes
      * 2. clear snakes killed
      * 3. check remained snakes if
      */
  }
}

object Utils {
  def minimumDistance(from: Vec2, to: Vec2): Vec2 = {
    from - to
  }
}