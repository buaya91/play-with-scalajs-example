package test

import org.scalatest._
import shared.physics._

class PhysicsFormulaSuites extends WordSpec with Matchers {
  "Physic Engine" should {
    "find shortest vector from point to line" in {
      val line = NonXYLine(1, 3)
      val pt = Vec2.zero
      val resultV = PhysicsFormula.ptNormalIntersectionToLine(pt, line)

      resultV should equal(Vec2(-1.5, 1.5))
    }
    "find shortest vector from point to line segment" in {
      val segment = LineSegment(Vec2(0, 3), Vec2(20, 3))
      val pt = Vec2.zero
      val resultV = PhysicsFormula.shortestFromPointToLineSegment(pt, segment)
      resultV should equal(Vec2(0, 3))
    }
  }
}