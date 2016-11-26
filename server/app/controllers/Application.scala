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
import serializers._
import prickle._

import scala.util.{Failure, Success, Try}

class Application()(implicit actorSystem: ActorSystem, materializer: Materializer) extends Controller {

  val log = Logger(getClass)

  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

  // TODO: check id to ensure unique
  def gameChannel(id: String) = WebSocket.accept[String, String] { req =>
    wsFlow("Something")
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

    val coreLogicFlow = ActorFlow.actorRef(ref => GameLoopActor.props(shared.updateRate, ref))

    inputFlow.via(coreLogicFlow).via(serializeState)
  }
}
