package client.infrastructure

import monix.execution.Cancelable
import monix.reactive._
import org.scalajs.dom._
import boopickle.Default._
import shared.model.GameState
import shared.serializers.Serializers._
import scala.scalajs.js.typedarray._

trait GameStateSource {
  def src(): Observable[GameState]
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
}
