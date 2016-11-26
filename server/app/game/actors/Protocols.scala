package game.actors

import shared.core.IdentifiedGameInput
import shared.model.GameState

case object NextFrame

case class InitState(gameState: GameState)

case class UserInputs(inputs: Seq[IdentifiedGameInput])
