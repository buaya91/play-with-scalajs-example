package controllers

import java.nio.ByteBuffer

import akka.NotUsed
import akka.actor.{Actor, ActorSystem, DeadLetter, Props}
import akka.stream.Materializer
import akka.stream.scaladsl._
import game.actors.GameLoopActor
import play.api.libs.streams.ActorFlow
import play.api.{Environment, Logger}
import play.api.mvc.{Action, Controller, WebSocket}
import shared.SharedMessages
import shared.core.IdentifiedGameInput
import shared.model._
import shared.protocol.GameRequest
import shared.serializers.Serializers._
//import prickle._
import boopickle.Default._
import shared.physics.PhysicsFormula

import scala.util.{Failure, Random, Success, Try}

class Application()(implicit actorSystem: ActorSystem, materializer: Materializer) extends Controller {

  val log = Logger(getClass)

  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

  // TODO: check id to ensure unique
  def gameChannel(id: String) = WebSocket.accept[Array[Byte], Array[Byte]] { req =>
//    wsFlow("Something")
    wsTestFlow
  }

  def test = WebSocket.accept[Array[Byte], Array[Byte]] { req =>
    wsTestFlow
  }

  def wsTestFlow: Flow[Array[Byte], Array[Byte], NotUsed] = {
    val inputFlow: Flow[Array[Byte], IdentifiedGameInput, NotUsed] =
      Flow
        .fromFunction[Array[Byte], IdentifiedGameInput] { rawBytes =>
          val r = Unpickle[GameRequest]
            .fromBytes(ByteBuffer.wrap(rawBytes))
          IdentifiedGameInput("Test", r.cmd)
        }

    val serializeState: Flow[GameState, Array[Byte], NotUsed] =
      Flow.fromFunction[GameState, Array[Byte]] { st =>
        bbToArrayBytes(Pickle.intoBytes[GameState](st))
      }

    val testState = {
      val snakes = for {
        i <- 1 to 3
      } yield {
        val blocks = PhysicsFormula.findContiguousBlock(shared.terrainX, shared.terrainY)
        Snake(Random.nextInt().toString, blocks, Up)
      }
      GameState(snakes, Set.empty)
    }

    val coreLogicFlow =
      ActorFlow.actorRef(ref => GameLoopActor.props(shared.serverUpdateRate, ref, testState))

    inputFlow.via(coreLogicFlow).via(serializeState)
  }

  def wsFlow(id: String): Flow[Array[Byte], Array[Byte], NotUsed] = {
    val inputFlow: Flow[Array[Byte], IdentifiedGameInput, NotUsed] =
      Flow
        .fromFunction[Array[Byte], IdentifiedGameInput] { rawBytes =>
          val r = Unpickle[GameRequest]
            .fromBytes(ByteBuffer.wrap(rawBytes))
          IdentifiedGameInput(id, r.cmd)
        }

    val serializeState: Flow[GameState, Array[Byte], NotUsed] =
      Flow.fromFunction[GameState, Array[Byte]] { st =>
        bbToArrayBytes(Pickle.intoBytes[GameState](st))
      }

    val coreLogicFlow =
      ActorFlow.actorRef(ref => GameLoopActor.props(shared.serverUpdateRate, ref, GameState.init))

    inputFlow.via(coreLogicFlow).via(serializeState)
  }
}
