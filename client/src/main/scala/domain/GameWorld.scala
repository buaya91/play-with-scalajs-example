package domain

import domain.components._
import domain.systems.GameSystem

import scala.collection.mutable

class GameWorld(
                 val areaComponents: mutable.Map[String, Seq[Position]] = mutable.HashMap(),
                 val isSnakeComponents: mutable.Map[String, Boolean] = mutable.HashMap(),
                 val speedComponents: mutable.Map[String, Speed] = mutable.HashMap(),
                 val directionComponents: mutable.Map[String, Direction] = mutable.HashMap(),
                 val frameRate: Int = 30
               ) {

  // intent and event is private
  val intentComponents: mutable.Map[String, Intent] = mutable.HashMap()
  val eventComponents: mutable.ArrayBuffer[Event] = mutable.ArrayBuffer()

  val systems: mutable.ArrayBuffer[GameSystem] = mutable.ArrayBuffer()

  def add(id: String, area: Seq[Position]) = areaComponents.update(id, area)
  def add(id: String, isSnake: Boolean) = isSnakeComponents.update(id, isSnake)
  def add(id: String, speed: Speed) = speedComponents.update(id, speed)
  def add(id: String, direction: Direction) = directionComponents.update(id, direction)
  def add(id: String, intent: Intent) = intentComponents.update(id, intent)

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
