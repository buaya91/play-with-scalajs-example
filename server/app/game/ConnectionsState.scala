package game

import akka.actor.ActorRef
import shared.protocol.{GameResponse, GameState, GameStateDelta}

/**
  * 1. when connection established, => pendingConn
  * 2. when player join => pendingState
  * 3. when player got state => joinedPlayer
  */

case class ConnectionsState(establishedConn: Map[String, ActorRef],
                            joinedPlayers: Map[String, ActorRef],
                            pendingState: Map[String, ActorRef]) {

  def open(id: String, connection: ActorRef): ConnectionsState = {
    copy(establishedConn + (id -> connection))
  }

  def join(id: String): ConnectionsState = {
    val (pendingUpdated, joinedUpdated) = establishedConn.get(id) match {
      case Some(c) => (establishedConn - id, joinedPlayers + (id -> c))
      case None    => (establishedConn, joinedPlayers)
    }
    copy(establishedConn = pendingUpdated, pendingState = joinedUpdated)
  }

  def close(id: String): ConnectionsState = {
    copy(establishedConn - id)
  }

  def broadcast(response: GameResponse): Unit = {
    joinedPlayers.foreach(_._2 ! response)
  }

  def broadcastState(state: GameState): Unit = {
    pendingState.foreach(_._2 ! state)
  }

  def clearPendingState: ConnectionsState = copy(establishedConn, joinedPlayers ++ pendingState, Map.empty)
}
