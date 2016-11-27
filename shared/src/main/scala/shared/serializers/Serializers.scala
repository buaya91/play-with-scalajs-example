package shared.serializers

//import prickle.{CompositePickler, PicklerPair}
import java.nio.ByteBuffer

import shared.model.{Direction, Down, GameState, Left, Right, Up}
import shared.protocol.{ChangeDirection, GameCommand}
import boopickle.Default._

object Serializers {
  implicit val dirP = compositePickler[Direction]
    .addConcreteType[Up.type]
    .addConcreteType[Down.type]
    .addConcreteType[Left.type]
    .addConcreteType[Right.type]

  implicit val cmdP = compositePickler[GameCommand].addConcreteType[ChangeDirection]

  def bbToArrayBytes(buffer: ByteBuffer): Array[Byte] = {
    val data = Array.ofDim[Byte](buffer.remaining())
    buffer.get(data)
    data
  }
}
