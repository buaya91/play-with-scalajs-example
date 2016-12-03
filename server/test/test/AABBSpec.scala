package test

import org.scalacheck._
import org.scalatest._
import org.scalatest.prop._
import shared.physics._

class AABBSpec extends PropSpec with GeneratorDrivenPropertyChecks with Matchers {
  def vec2Gen(x: Int) = for {
    d1 <- Gen.choose(0, x)
    d2 <- Gen.choose(0, x)
  } yield Vec2(d1, d2)

  val aabbGen: Gen[AABB] = for {
    v1 <- vec2Gen(500)
    v2 <- vec2Gen(50)
  } yield AABB(v1, v2)

  property("Minkowski AABB should be bigger than components in terms of area") {
    forAll(aabbGen, aabbGen) { (aabb1, aabb2) =>
      val minkowskiAABB = aabb1.minkowskiDiff(aabb2)
      minkowskiAABB.area should be >= (aabb1.area + aabb2.area)
    }
  }

  // generate aaab to ensure collision

  val collidingAABB = for {
    v1 <- vec2Gen(50)
  } yield AABB(v1, Vec2(50, 50))

  property("AABB should detect collision") {
    forAll(collidingAABB, collidingAABB) { (a, b) =>
      a.collided(b) shouldEqual(true)
    }
  }
}
