package game.actors

import java.util.UUID

import ai.{AIJoinGame, OffensiveAI}
import akka.actor.{Actor, Props}
import game.{ConnectionsState, ServerGameState}
import shared.core.IdentifiedGameInput
import shared._
import shared.protocol.{AssignedID, GameStateDelta, JoinGame, LeaveGame}

import scala.concurrent.duration._
import scala.language.postfixOps

class GameProxyActor extends Actor {

  List("John", "May").foreach(name => {
    val connectionID = UUID.randomUUID().toString
    val aiActor      = context.actorOf(OffensiveAI.props(connectionID, name))
    self ! ConnectionEstablished(connectionID, aiActor)
    aiActor ! AIJoinGame
  })

  context.system.scheduler.scheduleOnce(millisNeededPerUpdate millis, self, NextFrame)(context.dispatcher)

  private def updateState(connectionsState: ConnectionsState, serverState: ServerGameState) = {
    context.become(running(connectionsState, serverState))
  }

  override def receive: Receive = running(ConnectionsState(Map.empty, Map.empty, Map.empty), ServerGameState())

  def running(connectionsState: ConnectionsState, serverState: ServerGameState): Receive = {
    case input: IdentifiedGameInput =>
      val updatedConnections = input.cmd match {
        case j: JoinGame =>
          val id = input.playerID
          connectionsState.establishedConn.get(id).foreach(_ ! AssignedID(id))
          connectionsState.joinedPlayers.get(id).foreach(_ ! AssignedID(id))
          connectionsState.join(id)
        case _ =>
          connectionsState
      }

      updateState(updatedConnections, serverState.queueInput(input))

    case NextFrame =>
      val startTime = System.currentTimeMillis()
      val inputs = serverState.toSend.collect {
        case (frameN, i) if i.nonEmpty => GameStateDelta(i.values.toSeq, frameN)
      }

      inputs.foreach(connectionsState.broadcast)
      connectionsState.broadcastState(serverState.predictedState)

      val timeTaken = System.currentTimeMillis() - startTime
      val toWait  = Math.max(0, millisNeededPerUpdate - timeTaken - 20)

      context.system.scheduler.scheduleOnce(toWait millis, self, NextFrame)(context.dispatcher)

      updateState(connectionsState.clearPendingState, serverState.nextState)

    case ConnectionEstablished(id, ref) =>
      updateState(connectionsState.open(id, ref), serverState)

    case ConnectionClosed(id) =>
      val leaveGameInput     = IdentifiedGameInput(id, LeaveGame)
      val updatedState       = serverState.queueInput(leaveGameInput)
      val updatedConnections = connectionsState.close(id)
      updateState(updatedConnections, updatedState)
  }
}

object GameProxyActor {
  def props: Props =
    Props(classOf[GameProxyActor])
}
