package domain.components

sealed trait Intent

case class ChangeDirection(direction: Direction) extends Intent
case object SpeedUp extends Intent
