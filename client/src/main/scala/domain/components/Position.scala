package domain.components

case class Position(x: Double, y: Double) {
  def +(p: Position): Position = copy(x = this.x + p.x, y = this.y + p.y)

  def -(p: Position): Position = copy(x = this.x - p.x, y = this.y - p.y)

  def *(d: Double): Position = copy(x = this.x * d, y = this.y * d)
}
