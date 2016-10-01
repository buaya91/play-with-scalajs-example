package domain.components

import prickle._

sealed trait Direction

// case object Up extends Direction
//case object Down extends Direction
//case object Left extends Direction
//case object Right extends Direction

case class Up() extends Direction
case class Down() extends Direction
case class Left() extends Direction
case class Right() extends Direction

object Direction {
  implicit val dirPickler = CompositePickler[Direction]
    .concreteType[Up]
    .concreteType[Down]
    .concreteType[Left]
    .concreteType[Right]
}