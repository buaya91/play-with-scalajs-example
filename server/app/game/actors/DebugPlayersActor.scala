package game.actors

import akka.actor.{Actor, ActorRef, Props}
import shared.core.IdentifiedGameInput
import shared.protocol.{DebugNextFrame, JoinGame, GameState}

// todo: getting messy, either remove or refactor
class DebugPlayersActor(gameStateRef: ActorRef) extends Actor {

  override def receive: Receive = pendingGameState(Seq.empty)

  def pendingGameState(players: Seq[ActorRef]): Receive = {
    case s: GameState =>
      players.foreach(_ ! s)

    case debug @ IdentifiedGameInput("Debug", DebugNextFrame) =>
      gameStateRef ! NextFrame

    case input: IdentifiedGameInput =>
      gameStateRef ! input

    case ConnectionEstablished(id, r) =>
      gameStateRef ! IdentifiedGameInput(id, JoinGame("debug"))
      context.become(pendingGameState(players :+ r))
  }
}

object DebugPlayersActor {
  def props(gameStateRef: ActorRef): Props =
    Props(classOf[DebugPlayersActor], gameStateRef)
}
