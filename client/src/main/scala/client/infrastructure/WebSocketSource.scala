package client.infrastructure

import monix.execution.Cancelable
import monix.reactive._
import org.scalajs.dom._
import boopickle.Default._
import client.domain.AuthorityState
import shared.protocol._
import shared.protocol.GameRequest
import shared.serializers.Serializers._
import client.Utils
import scala.scalajs.js.typedarray._

trait WebSocketSource extends AuthorityState {
  val wsConn: WebSocket
  wsConn.binaryType = "arraybuffer"

  def stream(): Observable[GameResponse] = {
    Observable.create[GameResponse](OverflowStrategy.Unbounded) { sync =>
      wsConn.onmessage = (ev: MessageEvent) => {
        val rawBytes                           = TypedArrayBuffer.wrap(ev.data.asInstanceOf[ArrayBuffer])
        val deserializedResponse: GameResponse = Unpickle[GameResponse].fromBytes(rawBytes)
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

  override def request(input: GameRequest) = {
    val serialized = Pickle.intoBytes[GameRequest](input)
    val arrayBuf   = Utils.bbToArrayBuffer(serialized)

    wsConn.send(arrayBuf)
  }
}

object DefaultWSSource extends WebSocketSource {
  lazy val protocol             = if (window.location.protocol == "https:") "wss" else "ws"
  override lazy val wsConn = new WebSocket(s"$protocol://${window.location.host}/ws")
}
