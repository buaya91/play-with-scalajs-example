package shared.protocol

import shared.model.Direction

sealed trait GameRequest

// special case because we need to force leave when user disconnected
case object LeaveGame extends GameRequest
case object DebugNextFrame extends GameRequest
case class JoinGame(name: String) extends GameRequest

sealed trait SequencedGameRequest extends GameRequest {
  val seqNo: Int      // seqNo of GameState after
}

case class ChangeDirection(direction: Direction, seqNo: Int) extends SequencedGameRequest
case class SpeedUp(seqNo: Int) extends SequencedGameRequest
