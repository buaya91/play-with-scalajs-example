package ai

import shared.physics.AABB
import shared.protocol.{GameState, SequencedGameRequest}

trait SnakeAI {
  type Head = AABB
  val id: String
  def findNearestEnemy(state: GameState): Head = {
    ???
  }

  def react(latestState: GameState): SequencedGameRequest
}
