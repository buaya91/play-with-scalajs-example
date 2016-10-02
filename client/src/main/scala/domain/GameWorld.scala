package domain

import domain.components._
import domain.systems.GameSystem

import scala.collection.mutable

class GameWorld(
                 val areaComponents: mutable.Map[String, Seq[Position]] = mutable.HashMap(),
                 val isSnakeComponents: mutable.Map[String, Boolean] = mutable.HashMap(),
                 val speedComponents: mutable.Map[String, Speed] = mutable.HashMap(),
                 val directionComponents: mutable.Map[String, Direction] = mutable.HashMap()
               ) {

  // intent and event is private
  val intentComponents: mutable.Map[String, Intent] = mutable.HashMap()
  val eventComponents: mutable.ArrayBuffer[Event] = mutable.ArrayBuffer()

  val systems: mutable.ArrayBuffer[GameSystem] = mutable.ArrayBuffer()

  def add(id: String, area: Seq[Position]) = areaComponents.put(id, area)
  def add(id: String, isSnake: Boolean) = isSnakeComponents.put(id, isSnake)
  def add(id: String, speed: Speed) = speedComponents.put(id, speed)
  def add(id: String, direction: Direction) = directionComponents.put(id, direction)
  def add(id: String, intent: Intent) = intentComponents.put(id, intent)

  def addEvent(event: Event) = eventComponents += event

  def addSystem(sys: GameSystem) = systems += sys

  def remove(id: String) = {
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
