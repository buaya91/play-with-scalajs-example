package scala

import org.scalatest._
import shared.model._
import shared.physics._
import shared.protocol.GameState

class PhysicsFormulaSuites extends WordSpec with Matchers {
  "Physic Engine" should {
    "find shortest vector from point to line" in {
      val line    = NonXYLine(1, 3)
      val pt      = Vec2.zero
      val resultV = PhysicsFormula.ptNormalIntersectionToLine(pt, line)

      resultV should equal(Vec2(-1.5, 1.5))
    }
    "find shortest vector from point to line segment" in {
      val segment = LineSegment(Vec2(0, 3), Vec2(20, 3))
      val pt      = Vec2.zero
      val resultV = PhysicsFormula.shortestFromPointToLineSegment(pt, segment)
      resultV should equal(Vec2(0, 3))
    }

    "compute empty contiguous block in fixed area" in {

      def snakesCollided(a: Snake, b: Snake): Boolean = {
        (for {
          p1 <- a.body
          p2 <- b.body
        } yield {
          p1.collided(p2)
        }).contains(true)
      }

      var state = GameState.init

      for {
        i <- 0 to 100
      } yield {
        val block = PhysicsFormula.findContiguousBlock(state, 5)
        state = state.copy(state.snakes :+ Snake(i.toString, i.toString, block, Up))
      }

      state.snakes.size shouldBe 101

      val collisions = for {
        s1 <- state.snakes
        s2 <- state.snakes
        if s1 != s2
      } yield {
        snakesCollided(s1, s2)
      }

      collisions.count(_ == true) shouldBe 0
    }
  }
}
