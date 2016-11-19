package shared.api

import shared.model.Direction

sealed trait GameInput {
  val id: String
}

case class ChangeDirection(id: String, newDir: Direction) extends GameInput

