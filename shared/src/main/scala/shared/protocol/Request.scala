package shared.protocol

import shared.model.Direction

case class Request(frameNo: Int, cmd: GameCommand)

sealed trait GameCommand
case class ChangeDirection(direction: Direction) extends GameCommand