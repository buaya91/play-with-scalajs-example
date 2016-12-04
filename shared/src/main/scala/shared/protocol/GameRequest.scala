package shared.protocol

import shared.model.Direction

case class GameRequest(cmd: GameCommand)

sealed trait GameCommand
case object JoinGame extends GameCommand
case object LeaveGame extends GameCommand
case class ChangeDirection(direction: Direction) extends GameCommand

case object DebugNextFrame extends GameCommand
