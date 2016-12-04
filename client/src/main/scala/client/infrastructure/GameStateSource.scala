package client.infrastructure

import java.nio.ByteBuffer

import monix.execution.Cancelable
import monix.reactive._
import org.scalajs.dom._
import boopickle.Default._
import shared.model.GameState
import shared.protocol.GameRequest
import shared.serializers.Serializers._

import scala.scalajs.js.typedarray._

trait GameStateSource {
  def src(): Observable[GameState]
  def send(input: GameRequest): Unit

  protected def bbToArrayBuffer(buffer: ByteBuffer): ArrayBuffer = {
    val arrayBytes = bbToArrayBytes(buffer)
    val arrayBuf = new ArrayBuffer(arrayBytes.length)

    val typedAB = TypedArrayBuffer.wrap(arrayBuf)
    typedAB.put(arrayBytes)

    arrayBuf
  }
}

object SourceForTest extends GameStateSource {
  val wsConn = new WebSocket("ws://localhost:9000/wstest")

  wsConn.binaryType = "arraybuffer"

  override def src() = {
    Observable.create[GameState](OverflowStrategy.Unbounded) { sync =>
      wsConn.onmessage = (ev: MessageEvent) => {
        val rawBytes = TypedArrayBuffer.wrap(ev.data.asInstanceOf[ArrayBuffer])
        val deserializedGameState: GameState = Unpickle[GameState].fromBytes(rawBytes)
        sync.onNext(deserializedGameState)
      }

      Cancelable(() => {
        sync.onComplete()
        wsConn.close()
      })
    }
  }

  override def send(input: GameRequest) = {
    val serialized = Pickle.intoBytes(input)
    val arrayBuf = bbToArrayBuffer(serialized)

    wsConn.send(arrayBuf)
  }
}

object DebugSource extends GameStateSource {
  val wsConn = new WebSocket("ws://localhost:9000/wsdebug")

  wsConn.binaryType = "arraybuffer"

  override def src() = {
    Observable.create[GameState](OverflowStrategy.Unbounded) { sync =>
      wsConn.onmessage = (ev: MessageEvent) => {
        val rawBytes = TypedArrayBuffer.wrap(ev.data.asInstanceOf[ArrayBuffer])
        val deserializedGameState: GameState = Unpickle[GameState].fromBytes(rawBytes)
        sync.onNext(deserializedGameState)
      }

      Cancelable(() => {
        sync.onComplete()
        wsConn.close()
      })
    }
  }

  override def send(input: GameRequest) = {
    val serialized = Pickle.intoBytes(input)
    val arrayBuf = bbToArrayBuffer(serialized)

    wsConn.send(arrayBuf)
  }
}
