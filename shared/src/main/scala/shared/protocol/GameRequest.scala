package shared.protocol

import shared.model.Direction

sealed trait GameRequest
case class JoinGame(name: String) extends GameRequest
case object LeaveGame extends GameRequest
case object DebugNextFrame extends GameRequest

sealed trait GameCommand extends GameRequest {
  val sequenceNo: Int
}
case class ChangeDirection(direction: Direction, sequenceNo: Int = 0) extends GameCommand
case class SpeedUp(sequenceNo: Int = 0) extends GameCommand
