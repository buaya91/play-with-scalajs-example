package shared.protocol

import shared.model.Direction

sealed trait GameRequest

sealed trait GameCommand extends GameRequest
case class JoinGame(name: String) extends GameCommand
case object LeaveGame extends GameCommand
case class ChangeDirection(direction: Direction) extends GameCommand
case object SpeedUp extends GameCommand

case object DebugNextFrame extends GameCommand
