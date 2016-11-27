package shared.physics

import Vec2._

import scala.util.Random

object PhysicsFormula {
  def ptNormalIntersectionToLine(pt: Vec2, line: Line): Vec2 = {
    line match {
      case XLine(y) => Vec2(pt.x, y)
      case YLine(x) => Vec2(x, pt.y)
      case NonXYLine(m, c) =>
        val normalGradient = -1 / m
        val normalC = pt.y - (normalGradient * pt.x)

        val intersectX = (normalC - c) / (m - normalGradient)
        val intersectY = m * intersectX + c

        Vec2(intersectX, intersectY)
    }
  }

  def shortestFromPointToLineSegment(point: Vec2, segment: LineSegment): Vec2 = {
    val from = segment.from
    val to = segment.to

    val intersection = ptNormalIntersectionToLine(point, segment.toLine)
    val pointInSegment = (intersection >= from && intersection <= to) ||
        (intersection >= to && intersection <= from)

    if (pointInSegment)
      intersection - point
    else {
      val distanceFrom = (from - point).magnitude
      val distanceTo = (to - point).magnitude
      if (distanceFrom < distanceTo)
        from
      else
        to
    }
  }

  def isPointInsideAABB(point: Vec2, aABB: AABB): Boolean = {
    val pX = point.x
    val pY = point.y

    val maxX = aABB.bottomRight.x
    val minX = aABB.topLeft.x

    val minY = aABB.topLeft.y
    val maxY = aABB.bottomRight.y

    (pX > minX && pX < maxX) && (pY > minY && pY < maxY)
  }

  def minimumDistance(from: Vec2, to: Vec2): Vec2 = {
    from - to
  }

  def randomPositive(): Int = Math.abs(Random.nextInt())

  def findContiguousBlock(x: Double, y: Double): Seq[AABB] = {

    val randomPt = (randomPositive() % (x - 5) + 5, randomPositive() % (y - 5) + 5)

    for {
      i <- 0 to 5
    } yield {
      val c = Vec2(randomPt._1 + i, randomPt._2)
      AABB(c, Vec2(1, 1))
    }
  }
}
