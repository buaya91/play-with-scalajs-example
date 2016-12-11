package game.actors

import akka.actor.{Actor, ActorRef, Props}
import shared.core.IdentifiedGameInput
import shared.model.GameState
import shared.protocol.{JoinGame, LeaveGame}

import scala.concurrent.duration._
import scala.language.postfixOps

class PlayersActor(loopPerSec: Int, gameStateRef: ActorRef) extends Actor {

  private lazy val millisPerUpdate = 1000 / loopPerSec

  override def receive: Receive = waitingConnection

  def gameActive(frameStart: Long, players: Map[String, ActorRef]): Receive = {
    case s: GameState =>
      players.values.foreach(_ ! s)

      val millisToWait = timeToNextFrame(frameStart)

      if (millisToWait < 0)
        println(s"Opps: delayed $millisToWait")
//      assert(millisToWait >= 0, s"Millis to wait is $millisToWait")

      context.system.scheduler
        .scheduleOnce(Math.max(0, millisToWait) millis, gameStateRef, NextFrame)(context.dispatcher, self)

      context.become(gameActive(System.currentTimeMillis() + millisToWait, players))

    case input: IdentifiedGameInput =>
      gameStateRef ! input

    case ConnectionEstablished(id, r) =>
      context.become(gameActive(frameStart, players + ((id, r))))

    case ConnectionClosed(id) =>
      gameStateRef ! IdentifiedGameInput(id, LeaveGame)
      val removed = players - id

      if (removed.isEmpty)
        context.become(waitingConnection)
      else
        context.become(gameActive(frameStart, removed))
  }

  def waitingConnection: Receive = {
    case ConnectionEstablished(id, r) =>
      context.become(waitingPlayerJoinGame(Map(id -> r)))
  }

  def waitingPlayerJoinGame(connections: Map[String, ActorRef]): Receive = {
    case ConnectionEstablished(id, r) =>
      context.become(waitingPlayerJoinGame(connections + ((id, r))))

    case i @ IdentifiedGameInput(_, JoinGame(_)) =>
      gameStateRef ! i
      context.become(gameActive(System.currentTimeMillis(), connections))
      gameStateRef ! NextFrame

    case what =>
      println(s"$what did I rc?")
  }

  def timeToNextFrame(lastFrameMillis: Long): Long = {
    val now = System.currentTimeMillis()
    val millisToWait = lastFrameMillis + millisPerUpdate - now
    millisToWait
  }
}

object  PlayersActor {
  def props(updateRate: Int, gameStateRef: ActorRef): Props =
    Props(classOf[PlayersActor], updateRate, gameStateRef)
}
