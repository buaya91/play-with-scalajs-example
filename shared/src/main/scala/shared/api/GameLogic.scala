package shared.api

import shared.model.GameState

/**
  * @author limqingwei
  */
trait GameLoop {
  def step(state: GameState, inputs: Set[GameInput]): GameState = {

  }
}
