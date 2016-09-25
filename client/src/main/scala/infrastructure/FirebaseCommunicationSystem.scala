package infrastructure

import domain.GameWorld
import domain.systems.CommunicationSystem
class FirebaseCommunicationSystem() extends CommunicationSystem {

  override def process(world: GameWorld): Unit = {
    val events = world.eventComponents


  }
}
