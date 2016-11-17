package domain

import shared.model.Position

sealed trait Snacks {
  val position: Position
  val size: Int
}

case class Chocolate(position: Position) extends Snacks {
  val size: Int = 2
}
