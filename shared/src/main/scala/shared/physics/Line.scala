package shared.physics

sealed trait Line
case class XLine(y: Double) extends Line
case class YLine(x: Double) extends Line
case class NonXYLine(m: Double, c: Double) extends Line

case class LineSegment(from: Vec2, to: Vec2) {
  lazy val toLine: Line = {
    val diff = from - to
    diff match {
      case Vec2(0, _) => YLine(from.x)
      case Vec2(_, 0) => XLine(from.y)
      case _ =>
        val m = diff.y / diff.x
        val c = from.y - (m * from.x)
        NonXYLine(m, c)
    }
  }
}