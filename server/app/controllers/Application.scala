package controllers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl._
import game.GameLoop
import play.api.{Environment, Logger}
import play.api.mvc.{Action, Controller, WebSocket}
import shared.SharedMessages
import shared.core.IdentifiedGameInput
import shared.model._
import shared.protocol.{ChangeDirection, GameCommand, GameRequest}

import prickle._

import scala.util.{Failure, Success, Try}

class Application()(implicit environment: Environment, actorSystem: ActorSystem, materializer: Materializer)
    extends Controller {

  implicit val dirP: PicklerPair[Direction] = CompositePickler[Direction]
    .concreteType[Up.type]
    .concreteType[Down.type]
    .concreteType[Left.type]
    .concreteType[Right.type]

  implicit val cmdP: PicklerPair[GameCommand] = CompositePickler[GameCommand].concreteType[ChangeDirection]

  val log = Logger(getClass)

  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

  // TODO: check id to ensure unique
  def gameChannel(id: String) = WebSocket.accept[String, String] { req =>
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

    inputFlow.via(GameLoop.start(30)).via(serializeState)
  }
}
