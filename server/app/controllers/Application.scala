package controllers

import java.nio.ByteBuffer
import java.util.UUID

import akka.NotUsed
import akka.actor._
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl._
import game.actors._
import play.api.Logger
import play.api.mvc.{Action, Controller, WebSocket}
import shared.core.IdentifiedGameInput
import shared.protocol._
import shared.serializers.Serializers._
import boopickle.Default._

import scala.concurrent.ExecutionContext

//import scala.concurrent.duration._

class Application(timerEc: ExecutionContext)(implicit actorSystem: ActorSystem, materializer: Materializer) extends Controller {

  val log            = Logger(getClass)
  val gameProxyActor = actorSystem.actorOf(GameProxyActor.props(timerEc))

  def index = Action {
    Ok(views.html.main("Snake"))
  }

  def gameChannel = WebSocket.accept[Array[Byte], Array[Byte]] { req =>
    val connectionID = UUID.randomUUID()
    wsFlow(connectionID.toString)
  }

  def wsFlow(id: String): Flow[Array[Byte], Array[Byte], NotUsed] = {

    val deserializeState: Flow[Array[Byte], IdentifiedGameInput, NotUsed] =
      Flow.fromFunction[Array[Byte], IdentifiedGameInput] { rawBytes =>
        val req = Unpickle[GameRequest].fromBytes(ByteBuffer.wrap(rawBytes))

        IdentifiedGameInput(id, req)
      }

    val serializeState: Flow[GameResponse, Array[Byte], NotUsed] =
      Flow.fromFunction[GameResponse, Array[Byte]] { st =>
        bbToArrayBytes(Pickle.intoBytes[GameResponse](st))
      }

    val coreLogicFlow: Flow[IdentifiedGameInput, GameResponse, NotUsed] = {
      val out =
        Source
          .actorRef[GameResponse](1000000, OverflowStrategy.dropNew)
          .mapMaterializedValue(ref => gameProxyActor ! ConnectionEstablished(id, ref))

      val in: Sink[IdentifiedGameInput, NotUsed] =
        Sink.actorRef(gameProxyActor, ConnectionClosed(id))

      Flow.fromSinkAndSource(in, out)
    }

    deserializeState.via(coreLogicFlow).via(serializeState)
  }
}
