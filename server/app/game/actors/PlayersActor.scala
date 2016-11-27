package game.actors

import akka.actor.{Actor, ActorRef, Props}
import shared.core.IdentifiedGameInput
import shared.model.GameState
import shared.protocol.JoinGame

import scala.concurrent.duration._
import scala.language.postfixOps

class PlayersActor(loopPerSec: Int, gameStateRef: ActorRef) extends Actor {

  private lazy val millisPerUpdate = 1000 / loopPerSec

  gameStateRef ! NextFrame

  override def receive: Receive = pendingGameState(System.currentTimeMillis(), Seq.empty)

  def pendingGameState(frameStart: Long, players: Seq[ActorRef]): Receive = {
    case s: GameState =>
      players.foreach(_ ! s)

      val millisToWait = timeToNextFrame(frameStart)

      if (millisToWait < 0)
        println(s"Opps: delayed $millisToWait")
//      assert(millisToWait >= 0, s"Millis to wait is $millisToWait")

      context.system.scheduler
        .scheduleOnce(Math.max(0, millisToWait) millis, gameStateRef, NextFrame)(context.dispatcher, self)

      context.become(pendingGameState(System.currentTimeMillis() + millisToWait, players))

    case input: IdentifiedGameInput =>
      gameStateRef ! UserInputs(Seq(input))

    case PlayerJoin(id, r) =>
      gameStateRef ! IdentifiedGameInput(id, JoinGame)
      context.become(pendingGameState(frameStart, players :+ r))
  }

  def timeToNextFrame(lastFrameMillis: Long): Long = {
    val now = System.currentTimeMillis()
    val millisToWait = lastFrameMillis + millisPerUpdate - now
    millisToWait
  }
}

object PlayersActor {
  def props(updateRate: Int, gameStateRef: ActorRef): Props =
    Props(classOf[PlayersActor], updateRate, gameStateRef)
}
