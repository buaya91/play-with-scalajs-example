package domain.components

sealed trait Event

case class SnakedAdded(id: String, body: Seq[Position], direction: Direction, speed: Speed) extends Event
case class AppleAdded(id: String, position: Position) extends Event

case class EntityRemoved(id: String) extends Event
