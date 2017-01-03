package game

import java.util.UUID

import akka.actor.ActorRef
import shared.protocol.GameResponse

case class ConnectionsState(pendingConnections: Map[UUID, ActorRef], joinedPlayers: Map[UUID, ActorRef]) {

  def open(id: UUID, connection: ActorRef): ConnectionsState = {
    copy(pendingConnections + (id -> connection))
  }

  def join(id: UUID): ConnectionsState = {
    val (pendingUpdated, joinedUpdated) = pendingConnections.get(id) match {
      case Some(c) => (pendingConnections - id, joinedPlayers + (id -> c))
      case None    => (pendingConnections, joinedPlayers)
    }
    copy(pendingUpdated, joinedUpdated)
  }

  def remove(id: UUID): ConnectionsState = {
    copy(pendingConnections - id)
  }

  def broadcast(response: GameResponse): Unit = {
    pendingConnections.foreach(_._2 ! response)
  }
}
