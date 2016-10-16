package domain.systems

import domain.{GameRepo, GameWorld}
import domain.components._
import monix.execution.Scheduler.Implicits.global

import scala.collection.mutable.ArrayBuffer

trait CommunicationSystem extends GameSystem {
  val gameRepo: GameRepo

  private val globalEventsBuffer: ArrayBuffer[GlobalEvent] = ArrayBuffer()

  gameRepo.subscribeToAllEvents().foreach(globalEventsBuffer.append(_))

  override def process(world: GameWorld): Unit = {
    val events = world.eventComponents

    events.foreach {
      case Collision(collidentA, collidentB) =>
      case global: GlobalEvent => gameRepo.broadcastEvent(global)
      case _ => // todo: handle others
    }

    globalEventsBuffer.foreach {
      case SnakeAdded(id, body, dir, spd) =>
        println(s"$id is added")

        world.add(id, body)
        world.add(id, dir)
        world.add(id, spd)
        world.add(id, true)

      case ev @ EntityRemoved(id) =>
        world.remove(id)
      case DirectionChanged(id, dir) =>
        world.add(id, dir)
      case _ => // TODO: to be handle
    }

    globalEventsBuffer.clear()

    world.eventComponents.clear()
  }
}
