package shared.protocol

import shared.model.{Apple, Snake}

sealed trait GameResponse

case class GameState(snakes: Seq[Snake], apples: Set[Apple], seqNo: Int) extends GameResponse {
  def increaseSeqNo: GameState = this.copy(seqNo = seqNo + 1)
}

case class AssignedID(id: String) extends GameResponse

object GameState { def init = GameState(Seq.empty, Set.empty, 0) }
