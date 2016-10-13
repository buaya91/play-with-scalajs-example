package domain.systems

import domain.{GameRepo, GameWorld}
import domain.components.{Collision, GlobalEvent}

trait CommunicationSystem extends GameSystem {
  val gameRepo: GameRepo
  override def process(world: GameWorld): Unit = {
    val events = world.eventComponents

    events.foreach {
      case Collision(collidentA, collidentB) =>
      case global: GlobalEvent => gameRepo.broadcastEvent(global)
      case _ => // todo: handle others
    }

    world.eventComponents.clear()
  }
}
