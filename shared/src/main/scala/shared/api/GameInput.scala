package shared.api

import shared.model.Direction

sealed trait GameInput

case class ChangeDirection(newDir: Direction)

