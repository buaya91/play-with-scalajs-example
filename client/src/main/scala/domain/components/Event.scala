package domain.components

import prickle.CompositePickler

sealed trait Event

sealed trait GlobalEvent extends Event
case class SnakeAdded(id: String, body: Seq[Position], direction: Direction, speed: Speed) extends GlobalEvent
case class AppleAdded(id: String, position: Position) extends GlobalEvent
case class DirectionChanged(id: String, newDir: Direction) extends GlobalEvent
case class EntityRemoved(id: String) extends GlobalEvent

sealed trait LocalEvent extends Event
case class Collision(a: Seq[Position], b: Seq[Position]) extends LocalEvent

object GlobalEvent {
  implicit val eventPickler = CompositePickler[GlobalEvent]
    .concreteType[SnakeAdded]
    .concreteType[AppleAdded]
    .concreteType[DirectionChanged]
    .concreteType[EntityRemoved]
}