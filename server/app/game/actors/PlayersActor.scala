package game.actors

import akka.actor.{Actor, ActorRef, Props}
import shared.core.IdentifiedGameInput
import shared.protocol.{AssignedID, GameState, JoinGame, LeaveGame}

import scala.concurrent.duration._
import scala.language.postfixOps

class PlayersActor(loopPerSec: Int, gameStateRef: ActorRef) extends Actor {

  private lazy val millisPerUpdate = 1000 / loopPerSec

  override def receive: Receive = waitingConnection

  def gameActive(frameStart: Long,
                 connections: Map[String, ActorRef]): Receive = {
    case s: GameState =>
      connections.values.foreach(_ ! s)

      val millisToWait = timeToNextFrame(frameStart)

      if (millisToWait < 0)
        println(s"Opps: delayed $millisToWait")

      val nextTickIn = Math.max(0, millisToWait)

      context.system.scheduler.scheduleOnce(
        nextTickIn millis,
        gameStateRef,
        NextFrame)(context.dispatcher, self)

      context.become(
        gameActive(System.currentTimeMillis() + nextTickIn, connections))

    case input @ IdentifiedGameInput(id, cmd) =>
      gameStateRef ! input
      cmd match {
        case x: JoinGame => connections(id) ! AssignedID(id)
        case _ =>
      }

    case ConnectionEstablished(id, r) =>
      val updatedConn = connections + (id -> r)

      context.become(gameActive(frameStart, updatedConn))

    case ConnectionClosed(id) =>
      gameStateRef ! IdentifiedGameInput(id, LeaveGame)

      val removed = connections - id
      val nextState = {
        if (removed.isEmpty)
          waitingConnection
        else
          gameActive(frameStart, removed)
      }

      context.become(nextState)
  }

  def waitingConnection: Receive = {
    case ConnectionEstablished(id, r) =>
      context.become(waitingPlayerJoinGame(Map(id -> r)))
  }

  def waitingPlayerJoinGame(connections: Map[String, ActorRef]): Receive = {
    case ConnectionEstablished(id, r) =>
      context.become(waitingPlayerJoinGame(connections + ((id, r))))

    case i @ IdentifiedGameInput(id, j: JoinGame) =>
      connections(id) ! AssignedID(id)

      gameStateRef ! i
      gameStateRef ! NextFrame
      context.become(gameActive(System.currentTimeMillis(), connections))

    case ConnectionClosed(id) =>
      gameStateRef ! IdentifiedGameInput(id, LeaveGame)
      val removed = connections - id

      val nextState = {
        if (removed.isEmpty)
          waitingConnection
        else
          waitingPlayerJoinGame(removed)
      }
      context.become(nextState)
  }

  private def timeToNextFrame(lastFrameMillis: Long): Long = {
    val now = System.currentTimeMillis()
    val millisToWait = lastFrameMillis + millisPerUpdate - now
    millisToWait
  }
}

object PlayersActor {
  def props(updateRate: Int, gameStateRef: ActorRef): Props =
    Props(classOf[PlayersActor], updateRate, gameStateRef)
}
