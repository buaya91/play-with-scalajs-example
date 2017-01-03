package game

import akka.actor.ActorRef
import shared.protocol.GameResponse

case class ConnectionsState(pendingConnections: Map[String, ActorRef], joinedPlayers: Map[String, ActorRef]) {

  def open(id: String, connection: ActorRef): ConnectionsState = {
    copy(pendingConnections + (id -> connection))
  }

  def join(id: String): ConnectionsState = {
    val (pendingUpdated, joinedUpdated) = pendingConnections.get(id) match {
      case Some(c) => (pendingConnections - id, joinedPlayers + (id -> c))
      case None    => (pendingConnections, joinedPlayers)
    }
    copy(pendingUpdated, joinedUpdated)
  }

  def close(id: String): ConnectionsState = {
    copy(pendingConnections - id)
  }

  def broadcast(response: GameResponse): Unit = {
    joinedPlayers.foreach(_._2 ! response)
  }
}
