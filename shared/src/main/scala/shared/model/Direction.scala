package shared.model

sealed trait Direction {
  def isOppositeOf(direction: Direction): Boolean
}

case object Up extends Direction {
  override def isOppositeOf(direction: Direction) = direction == Down
}
case object Down extends Direction {
  override def isOppositeOf(direction: Direction) = direction == Up
}
case object Left extends Direction {
  override def isOppositeOf(direction: Direction) = direction == Right
}
case object Right extends Direction {
  override def isOppositeOf(direction: Direction) = direction == Left
}
