package shared.model.core

import shared.physics.Vec2

trait AABB {
  val center: Vec2
  val halfExtents: Vec2
  lazy val topLeft = center - halfExtents
  lazy val bottomRight = center + halfExtents
  lazy val topRight = center + Vec2(halfExtents.x, -halfExtents.y)
  lazy val bottomLeft = center + Vec2(-halfExtents.x, +halfExtents.y)

  lazy val sideLineSegments: Seq[LineSegment] = Seq(
    LineSegment(topLeft, topRight),
    LineSegment(topLeft, bottomLeft),
    LineSegment(topRight, bottomRight),
    LineSegment(bottomLeft, bottomRight)
  )

  def minkowskiSum(that: AABB): AABB = {
    val h = halfExtents + that.halfExtents

    new AABB {
      override def translate(m: Vec2): AABB = this
      override val halfExtents: Vec2 = h
      override val center: Vec2 = that.center
    }
  }

  def minkowskiDiff(that: AABB): AABB = {
    val topL = topLeft - that.bottomRight
    val newExtents = halfExtents + that.halfExtents
    new AABB {
      val center = topL + newExtents
      val halfExtents = newExtents

      override def translate(m: Vec2): AABB = this
    }
  }

  def collided(aABB: AABB): Boolean = {
    val mSum = minkowskiSum(aABB)

    // shrink this, thus use this.center
    PhysicEngine.isPointInsideAABB(this.center, mSum)
  }

  def area: Double = {
    val fullExtents = halfExtents * 2
    fullExtents.x * fullExtents.y
  }

  def penetrationVec(that: AABB): Vec2 = {
    val mSum = minkowskiSum(that)
    val originToSegments = mSum.sideLineSegments
      .map(segment => PhysicEngine.shortestFromPointToLineSegment(this.center, segment))
      .sortWith((a, b) => a.magnitude < b.magnitude)

    originToSegments.head
  }

  def relativeVelocity(that: AABB): Vec2 = {
    that.velocity - velocity
  }

  def translate(m: Vec2): AABB
}

object AABB {
  def apply(c: Vec2, h: Vec2, v: Vec2 = Vec2.zero): AABB = new AABB {

    override def translate(m: Vec2): AABB = this

    override val center: Vec2 = c
    override val velocity: Vec2 = v
    override val halfExtents: Vec2 = h
  }
}
