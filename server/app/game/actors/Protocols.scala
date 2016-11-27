package game.actors

import akka.actor.ActorRef
import shared.core.IdentifiedGameInput
import shared.model.GameState

case object NextFrame

case class InitState(gameState: GameState)

case class UserInputs(inputs: Seq[IdentifiedGameInput])

case class PlayerJoin(id: String, ref: ActorRef)
