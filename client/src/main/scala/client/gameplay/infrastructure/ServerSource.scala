package client.gameplay.infrastructure

import java.nio.ByteBuffer

import monix.execution.Cancelable
import monix.reactive._
import org.scalajs.dom._
import boopickle.Default._
import shared.protocol._
import shared.protocol.GameRequest
import shared.serializers.Serializers._

import scala.scalajs.js.typedarray._

trait ServerSource {
  val wsConn: WebSocket
  wsConn.binaryType = "arraybuffer"

  private var debugC = 0

  def src(): Observable[GameResponse] = {
    Observable.create[GameResponse](OverflowStrategy.Unbounded) { sync =>
      wsConn.onmessage = (ev: MessageEvent) => {
        val rawBytes = TypedArrayBuffer.wrap(ev.data.asInstanceOf[ArrayBuffer])
        val deserializedResponse: GameResponse = Unpickle[GameResponse].fromBytes(rawBytes)

        if (debugC == 0) {
          println(s"First r: $deserializedResponse")
          debugC = 1
        }

        sync.onNext(deserializedResponse)
      }

      wsConn.onerror = (ev: ErrorEvent) => {
        sync.onError(new Exception(ev.message))
      }

      Cancelable(() => {
        sync.onComplete()
        wsConn.close()
      })
    }
  }

  def send(input: GameRequest): Unit = {
    val serialized = Pickle.intoBytes[GameRequest](input)
    val arrayBuf = bbToArrayBuffer(serialized)

    wsConn.send(arrayBuf)
  }

  protected def bbToArrayBuffer(buffer: ByteBuffer): ArrayBuffer = {
    val arrayBytes = bbToArrayBytes(buffer)
    val arrayBuf = new ArrayBuffer(arrayBytes.length)

    val typedAB = TypedArrayBuffer.wrap(arrayBuf)
    typedAB.put(arrayBytes)

    arrayBuf
  }
}

object  ServerSource extends ServerSource {
  override lazy val wsConn = new WebSocket("ws://localhost:9000/ws")
}

object DebugSource extends ServerSource {
  override lazy val wsConn = new WebSocket("ws://localhost:9000/wsdebug")
}
