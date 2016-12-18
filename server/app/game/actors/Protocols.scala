package game.actors

import akka.actor.ActorRef
import shared.protocol.GameState

case object NextFrame

case class InitState(gameState: GameState) extends AnyVal

case class ConnectionEstablished(connectionID: String, ref: ActorRef)

case class ConnectionClosed(connectionID: String) extends AnyVal
