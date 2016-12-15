package shared.protocol

import shared.model.Direction

sealed trait GameRequest
case class JoinGame(name: String) extends GameRequest
case object LeaveGame extends GameRequest
case object DebugNextFrame extends GameRequest

sealed trait GameCommand extends GameRequest
case class ChangeDirection(direction: Direction) extends GameCommand
case object SpeedUp extends GameCommand
