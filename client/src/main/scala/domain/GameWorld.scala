package domain

import domain.components._
import domain.systems.GameSystem

import scala.collection.mutable

class GameWorld {
  type EntityId = String
  type Area = Seq[Position]

  val areaComponents: mutable.Map[EntityId, Area] = mutable.HashMap()
  val isSnakeComponents: mutable.Map[EntityId, Boolean] = mutable.HashMap()
  val speedComponents: mutable.Map[EntityId, Speed] = mutable.HashMap()
  val directionComponents: mutable.Map[EntityId, Direction] = mutable.HashMap()
  val intentComponents: mutable.Map[EntityId, Intent] = mutable.HashMap()

  val eventComponents: mutable.Seq[Event] = mutable.Seq()

  val systems: mutable.Seq[GameSystem] = mutable.Seq()

  def add(id: EntityId, area: Area) = areaComponents.put(id, area)
  def add(id: EntityId, isSnake: Boolean) = isSnakeComponents.put(id, isSnake)
  def add(id: EntityId, speed: Speed) = speedComponents.put(id, speed)
  def add(id: EntityId, direction: Direction) = directionComponents.put(id, direction)
  def add(id: EntityId, intent: Intent) = intentComponents.put(id, intent)

  def addEvent(event: Event) = eventComponents :+ event

  def addSystem(sys: GameSystem) = systems :+ sys

  def remove(id: EntityId) = {
    areaComponents.remove(id)
    speedComponents.remove(id)
    directionComponents.remove(id)
    intentComponents.remove(id)
    eventComponents :+ EntityRemoved(id)
  }

  def process(): Unit = {
    systems.foreach(_.process(this))
  }
}
