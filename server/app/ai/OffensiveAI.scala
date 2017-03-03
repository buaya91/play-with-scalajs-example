package ai

import akka.actor.{Actor, Props}
import shared.core.IdentifiedGameInput
import shared.model.{Direction, Snake, Up}
import shared.protocol.{ChangeDirection, GameState, JoinGame}

class OffensiveAI(val id: String, name: String) extends SnakeAI with Actor {
  private def closestDir(self: Snake, enemy: Snake): Direction = {
    Up
  }

  override def react(latestState: GameState) = {
    for {
      self  <- latestState.snakes.find(_.id == id)
      enemy <- findNearestEnemy(self, latestState)
    } yield {
      val dir = closestDir(self, enemy)
      ChangeDirection(dir, latestState.seqNo + 1)
    }
  }

  override def receive = {
    case st: GameState =>
      react(st).foreach { i =>
        sender() ! IdentifiedGameInput(id, i)
      }

    case AIJoinGame =>
      context.parent ! IdentifiedGameInput(id, JoinGame(name))
  }
}

object OffensiveAI {
  def props(id: String, name: String): Props = Props(classOf[OffensiveAI], id, name)
}

object AIJoinGame
