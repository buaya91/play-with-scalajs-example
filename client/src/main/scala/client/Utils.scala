package client

import java.nio.ByteBuffer
import shared.serializers.Serializers.bbToArrayBytes
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

object Utils {
  def bbToArrayBuffer(buffer: ByteBuffer): ArrayBuffer = {
    val arrayBytes = bbToArrayBytes(buffer)
    val arrayBuf   = new ArrayBuffer(arrayBytes.length)

    val typedAB = TypedArrayBuffer.wrap(arrayBuf)
    typedAB.put(arrayBytes)

    arrayBuf
  }
}
