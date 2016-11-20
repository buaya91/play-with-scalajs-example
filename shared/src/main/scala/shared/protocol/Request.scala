package shared.protocol

import shared.model.Direction

case class Request(cmd: GameCommand)

sealed trait GameCommand
case class ChangeDirection(direction: Direction) extends GameCommand