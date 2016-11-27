package client.infrastructure

import monix.execution.Cancelable
import monix.reactive._
import org.scalajs.dom._
import prickle._
import shared.model.GameState
import shared.serializers.Serializers._

import scala.util.{Failure, Success, Try}

trait GameStateSource {
  def subscribe(): Observable[GameState]
}

object SourceForTest extends GameStateSource {
  val wsConn = new WebSocket("ws://localhost:9000/wstest")

  override def subscribe() = {
    Observable.create[GameState](OverflowStrategy.Unbounded) { sync =>
      wsConn.onmessage = (ev: MessageEvent) => {
        val rawString = ev.data.toString
        val deserializedGameState: Try[GameState] = Unpickle[GameState].fromString(rawString)
        deserializedGameState match {
          case Success(s) => sync.onNext(s)
          case Failure(e) => println(s"Failed to deserialized: $e")
        }
      }

      Cancelable(() => {
        sync.onComplete()
        wsConn.close()
      })
    }
  }
}
