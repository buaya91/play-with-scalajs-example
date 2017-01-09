package shared.physics

import Vec2._
import shared._
import shared.protocol.GameState

import scala.util.Random

object PhysicsFormula {
  def ptNormalIntersectionToLine(pt: Vec2, line: Line): Vec2 = {
    line match {
      case XLine(y) => Vec2(pt.x, y)
      case YLine(x) => Vec2(x, pt.y)
      case NonXYLine(m, c) =>
        val normalGradient = -1 / m
        val normalC        = pt.y - (normalGradient * pt.x)

        val intersectX = (normalC - c) / (m - normalGradient)
        val intersectY = m * intersectX + c

        Vec2(intersectX, intersectY)
    }
  }

  def shortestFromPointToLineSegment(point: Vec2, segment: LineSegment): Vec2 = {
    val from = segment.from
    val to   = segment.to

    val intersection = ptNormalIntersectionToLine(point, segment.toLine)
    val pointInSegment = (intersection >= from && intersection <= to) ||
        (intersection >= to && intersection <= from)

    if (pointInSegment)
      intersection - point
    else {
      val distanceFrom = (from - point).magnitude
      val distanceTo   = (to - point).magnitude
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

  /**
    * 1. Create a lazy eval layer for each point that's occupied
    * 2. Pick a random starting point
    * 3. Start randomly move from starting point, memoize valid step
    * 4.
    */
  def findContiguousBlock(state: GameState, ln: Int): Seq[AABB] = {

    def randomPt() = Vec2(
      randomPositive() % (terrainX - ln) + ln,
      randomPositive() % (terrainY - ln) + ln
    )

    val oneSteps = Seq((1, 0), (-1, 0), (0, 1), (0, -1)).map(p => Vec2(p._1, p._2))

    def backtrackSearchContiguousBlock(currentTrack: Seq[AABB]): Option[Seq[AABB]] = {
      def stepOut(from: AABB): Seq[AABB] = {
        oneSteps
          .map(step => from.translate(step))
          .filter(moved => !currentTrack.contains(moved) && state.isEmpty(moved))
      }

      currentTrack match {
        case s if s.size == ln => Some(s)

        case Seq() =>
          var startPt = AABB(randomPt(), snakeBodyUnitSize)
          while (!state.isEmpty(startPt)) {
            startPt = AABB(randomPt(), snakeBodyUnitSize)
          }

          backtrackSearchContiguousBlock(Seq(startPt)) match {
            case x @ Some(_) => x
            case None        => backtrackSearchContiguousBlock(Seq.empty)
          }

        case x @ Seq(one) =>
          val possibleSteps                = stepOut(one)
          var validPath: Option[Seq[AABB]] = None
          val proceedableStep = possibleSteps.find(next => {
            validPath = backtrackSearchContiguousBlock(x :+ next)
            validPath.isDefined
          })

          // if not proceedable, rerun with empty
          proceedableStep match {
            case Some(_) => validPath
            case None    => None
          }

        case x @ (front :+ last) =>
          val possibleSteps = stepOut(last)
          var validPath: Option[Seq[AABB]] = None
          val proceedableStep = possibleSteps.find(next => {
            validPath = backtrackSearchContiguousBlock(x :+ next)
            validPath.isDefined
          })

          // if not proceedable, rerun with empty
          proceedableStep match {
            case Some(x) => validPath
            case None    => None
          }
      }
    }
    backtrackSearchContiguousBlock(Seq.empty).get
  }
}
