package shared.protocol

import shared.model.Direction

case class GameRequest(cmd: GameCommand)

sealed trait GameCommand
case class ChangeDirection(direction: Direction) extends GameCommand
