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
import scala.util.Random

trait GameStateSource {
  val wsConn: WebSocket
  wsConn.binaryType = "arraybuffer"

  def src(): Observable[GameState] = {
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

  def send(input: GameRequest): Unit = {
    val serialized = Pickle.intoBytes(input)
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

object GameStateSource extends GameStateSource {
  lazy val randomID = Random.nextString(5)
  lazy val wsConn = new WebSocket(s"ws://localhost:9000/ws/$randomID")
}

object SourceForTest extends GameStateSource {
  lazy val wsConn = new WebSocket("ws://localhost:9000/wstest")
}

object DebugSource extends GameStateSource {
  lazy val wsConn = new WebSocket("ws://localhost:9000/wsdebug")
}
