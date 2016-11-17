package shared.model.core

import shared.model.Position

case class Vec2(x: Double, y: Double) {

  def +(vec2: Vec2): Vec2 = Vec2(this.x + vec2.x, this.y + vec2.y)
  def *(x: Double) = Vec2(this.x * x, this.y * y)

  def -(vec2: Vec2) = {
    this + (vec2 * -1)
  }
}

object Vec2 {
  implicit def posToVec2(position: Position): Vec2 = Vec2(position.x, position.y)
  implicit def vec2ToPos(vec2: Vec2): Position = Position(vec2.x, vec2.y)
}