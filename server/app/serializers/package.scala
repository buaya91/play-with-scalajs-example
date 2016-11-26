import prickle.{CompositePickler, PicklerPair}
import shared.model.{Direction, Down, Left, Right, Up}
import shared.protocol.{ChangeDirection, GameCommand}

package object serializers {
  implicit val dirP: PicklerPair[Direction] = CompositePickler[Direction]
    .concreteType[Up.type]
    .concreteType[Down.type]
    .concreteType[Left.type]
    .concreteType[Right.type]

  implicit val cmdP: PicklerPair[GameCommand] = CompositePickler[GameCommand].concreteType[ChangeDirection]
}
