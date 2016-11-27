package game.actors

import akka.actor.{Actor, ActorRef, Props}
import shared.core.IdentifiedGameInput
import shared.model.GameState

import scala.concurrent.duration._
import scala.language.postfixOps

class GameLoopActor(loopPerSec: Int, subscriberRef: ActorRef, gameStateRef: ActorRef) extends Actor {

  private lazy val millisPerUpdate = 1000 / loopPerSec

  gameStateRef ! NextFrame

  override def receive: Receive = pendingResponse(System.currentTimeMillis())

  def pendingResponse(frameStart: Long): Receive = {
    case s: GameState =>
      subscriberRef ! s

      val millisToWait = timeToNextFrame(frameStart)

      assert(millisToWait >= 0, s"Millis to wait is $millisToWait")

      context.system.scheduler.scheduleOnce(millisToWait millis, gameStateRef, NextFrame)(context.dispatcher, self)

      context.become(pendingResponse(System.currentTimeMillis() + millisToWait))

    case input: IdentifiedGameInput =>
      gameStateRef ! UserInputs(Seq(input))
  }

  def timeToNextFrame(lastFrameMillis: Long): Long = {
    val now = System.currentTimeMillis()
    val millisToWait = lastFrameMillis + millisPerUpdate - now
    millisToWait
  }
}

object GameLoopActor {
  def props(updateRate: Int, subscribeRef: ActorRef, gameStateRef: ActorRef): Props =
    Props(classOf[GameLoopActor], updateRate, subscribeRef, gameStateRef)
}
