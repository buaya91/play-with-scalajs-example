package shared.protocol

import shared.model.{Apple, Snake}

sealed trait GameResponse

case class GameState(snakes: Seq[Snake], apples: Set[Apple]) extends GameResponse

case class AssignedID(id: String) extends GameResponse

object GameState { def init = GameState(Seq.empty, Set.empty) }
