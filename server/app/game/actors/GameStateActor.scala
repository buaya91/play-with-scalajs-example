package game.actors

import akka.actor.{Actor, Props}
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.model.GameState

class GameStateActor extends Actor {
  override def receive: Receive = unitialized

  def unitialized: Receive = {
    case InitState(state) => context.become(active(state, Seq.empty))
  }

  def active(state: GameState, inputs: Seq[IdentifiedGameInput]): Receive = {
    case UserInputs(i) => context.become(active(state, inputs ++ i))
    case NextFrame =>
      val updated = GameLogic.step(state, inputs)
      sender() ! updated

      context.become(active(updated, Seq.empty))
  }
}

object GameStateActor {
  def props: Props = Props[GameStateActor]
}
