package controllers

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
import prickle._
import shared.physics.PhysicsFormula

import scala.util.{Failure, Random, Success, Try}

class Application()(implicit actorSystem: ActorSystem, materializer: Materializer) extends Controller {

  val log = Logger(getClass)

  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

  // TODO: check id to ensure unique
  def gameChannel(id: String) = WebSocket.accept[String, String] { req =>
    wsFlow("Something")
  }

  def test = WebSocket.accept[String, String] { req =>
    wsTestFlow
  }

  def wsTestFlow: Flow[String, String, NotUsed] = {
    val inputFlow: Flow[String, IdentifiedGameInput, NotUsed] =
      Flow
        .fromFunction[String, Try[IdentifiedGameInput]](
          rawStr =>
            Unpickle[GameRequest]
              .fromString(rawStr)
              .map(r => IdentifiedGameInput("Test", r.cmd)))
        .collect[IdentifiedGameInput] { case Success(i) => i }

    val serializeState: Flow[GameState, String, NotUsed] =
      Flow.fromFunction[GameState, String](st => Pickle.intoString[GameState](st))

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
      ActorFlow.actorRef(ref => GameLoopActor.props(shared.updateRate, ref, testState))

    inputFlow.via(coreLogicFlow).via(serializeState)
  }

  def wsFlow(id: String): Flow[String, String, NotUsed] = {
    val deserializeReq: Flow[String, Try[IdentifiedGameInput], NotUsed] = Flow.fromFunction { rawStr =>
      Unpickle[GameRequest].fromString(rawStr).map(r => IdentifiedGameInput(id, r.cmd))
    }

    val deserializeFailure = deserializeReq.collect {
      case Failure(e) => log.error(e.getMessage)
    }

    val inputFlow: Flow[String, IdentifiedGameInput, NotUsed] = deserializeReq.collect[IdentifiedGameInput] {
      case Success(i) => i
    }

    val serializeState: Flow[GameState, String, NotUsed] =
      Flow.fromFunction[GameState, String](st => Pickle.intoString[GameState](st))

    val coreLogicFlow =
      ActorFlow.actorRef(ref => GameLoopActor.props(shared.updateRate, ref, GameState(Seq.empty, Set.empty)))

    inputFlow.via(coreLogicFlow).via(serializeState)
  }
}
