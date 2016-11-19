package shared.physics

case class Vec2(x: Double, y: Double) extends Ordered[Vec2] {

  def unary_- = Vec2(-x, -y)
  def +(vec2: Vec2): Vec2 = Vec2(x + vec2.x, y + vec2.y)
  def -(vec2: Vec2): Vec2 = Vec2(x - vec2.x, y - vec2.y)
  def *(multiplier: Double): Vec2 = Vec2(x * multiplier, y * multiplier)

  def magnitude: Double = Math.sqrt((x * x) + (y * y))
  def unitVector: Vec2 = Vec2(x / magnitude, y / magnitude)
  def dot(vec2: Vec2): Double = x * vec2.x + y * vec2.y

  override def compare(that: Vec2): Int = {
    if (x == that.x)
      (y - that.y).toInt
    else
      (x - that.x).toInt
  }
}

object Vec2 {
//  implicit def posToVec2(position: Square): Vec2 = Vec2(position.x, position.y)
//  implicit def vec2ToPos(vec2: Vec2): Square = Square(vec2.x, vec2.y)

  def zero = Vec2(0, 0)
}