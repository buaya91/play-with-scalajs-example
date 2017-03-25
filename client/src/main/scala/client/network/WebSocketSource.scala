package client.network

import boopickle.Default._
import client.Utils
import monix.execution.Cancelable
import monix.reactive._
import org.scalajs.dom._
import shared.protocol.{GameRequest, _}
import shared.serializers.Serializers._

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
