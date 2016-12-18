package shared.protocol

import shared.model.Direction

sealed trait GameRequest
case object LeaveGame extends GameRequest
case object DebugNextFrame extends GameRequest

sealed trait SequencedGameRequest extends GameRequest {
  val seqNo: Int      // seqNo of GameState after
}

case class JoinGame(name: String, seqNo: Int = 0) extends SequencedGameRequest
case class ChangeDirection(direction: Direction, seqNo: Int = 0) extends SequencedGameRequest
case class SpeedUp(seqNo: Int = 0) extends SequencedGameRequest
