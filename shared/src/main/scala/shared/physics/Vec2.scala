package shared.physics

case class Vec2(x: Double, y: Double) {

  def unary_- = Vec2(-x, -y)
  def +(vec2: Vec2): Vec2 = Vec2(x + vec2.x, y + vec2.y)
  def -(vec2: Vec2): Vec2 = Vec2(x - vec2.x, y - vec2.y)
  def *(multiplier: Double): Vec2 = Vec2(x * multiplier, y * multiplier)

  def magnitude: Double = Math.sqrt((x * x) + (y * y))
  def unitVector: Vec2 = Vec2(x / magnitude, y / magnitude)
  def dot(vec2: Vec2): Double = x * vec2.x + y * vec2.y


}

object Vec2 {
  def zero = Vec2(0, 0)

  implicit class OrderedVec2(vec2: Vec2) extends Ordered[Vec2] {
    override def compare(y: Vec2): Int = {
      if (vec2.x == y.x)
        (vec2.y - y.y).toInt
      else
        (vec2.x - y.x).toInt
    }
  }
}