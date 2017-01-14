package controllers

import java.nio.ByteBuffer

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import boopickle.Default._
import debug.{DebugPlayersActor, GameStateActor}
import game.actors._
import play.api.mvc.{Action, Controller, WebSocket}
import shared.core.IdentifiedGameInput
import shared.model.{Snake, Up}
import shared.physics.PhysicsFormula
import shared.protocol._
import shared.serializers.Serializers._
import shared.snakeBodyInitLength
import shared._

import scala.util.Random

class DebugApp()(implicit actorSystem: ActorSystem, materializer: Materializer)
    extends Controller {
  def debug = Action {
    Ok(views.html.debug("Debug"))
  }

  def debugWs = WebSocket.accept[Array[Byte], Array[Byte]] { req =>
    val debugGameState = actorSystem.actorOf(GameStateActor.props)

    val testState = {
      val snakes = for {
        i <- 1 to 3
      } yield {
        val blocks = PhysicsFormula.findContiguousBlock(GameState.init,
                                                        snakeBodyInitLength)
        Snake(Random.nextInt().toString, Random.nextString(3), blocks, Up)
      }
      GameState(snakes, Set.empty, 0)
    }
    debugGameState ! InitState(testState)
    val debugState =
      actorSystem.actorOf(DebugPlayersActor.props(debugGameState))

    val deserializeState: Flow[Array[Byte], IdentifiedGameInput, NotUsed] =
      Flow.fromFunction[Array[Byte], IdentifiedGameInput] { rawBytes =>
        val r = Unpickle[GameRequest].fromBytes(ByteBuffer.wrap(rawBytes))

        r match {
          case x: SequencedGameRequest => IdentifiedGameInput("Debug", x)
        }
      }

    val serializeState: Flow[GameState, Array[Byte], NotUsed] =
      Flow.fromFunction[GameState, Array[Byte]] { st =>
        bbToArrayBytes(Pickle.intoBytes[GameState](st))
      }

    val coreLogicFlow = {
      val out =
        Source
          .actorRef(1000000, OverflowStrategy.dropNew)
          .mapMaterializedValue(ref =>
            debugState ! ConnectionEstablished("Debug", ref))

      val in = Sink.actorRef(debugState, s"Debug ended")

      Flow.fromSinkAndSource(in, out)
    }

    deserializeState.via(coreLogicFlow).via(serializeState)
  }
}
