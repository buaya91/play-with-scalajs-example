package shared.physics

case class AABB(center: Vec2, halfExtents: Vec2) {
  lazy val topLeft = center - halfExtents
  lazy val bottomRight = center + halfExtents
  lazy val topRight = center + Vec2(halfExtents.x, -halfExtents.y)
  lazy val bottomLeft = center + Vec2(-halfExtents.x, +halfExtents.y)
  lazy val area: Double = {
    val fullExtents = halfExtents * 2
    fullExtents.x * fullExtents.y
  }

  lazy val sideLineSegments: Seq[LineSegment] = Seq(
    LineSegment(topLeft, topRight),
    LineSegment(topLeft, bottomLeft),
    LineSegment(topRight, bottomRight),
    LineSegment(bottomLeft, bottomRight)
  )

  def minkowskiSum(that: AABB): AABB = {
    val h = halfExtents + that.halfExtents

    this.copy(that.center, h)
  }

  def minkowskiDiff(that: AABB): AABB = {
    val topL = topLeft - that.bottomRight
    val newExtents = halfExtents + that.halfExtents
    this.copy(topL + newExtents, newExtents)
  }

  def collided(aABB: AABB): Boolean = {
    val mSum = minkowskiSum(aABB)

    // shrink this, thus use this.center
    PhysicsFormula.isPointInsideAABB(this.center, mSum)
  }

  def penetrationVec(that: AABB): Vec2 = {
    val mSum = minkowskiSum(that)
    val originToSegments = mSum.sideLineSegments
      .map(segment => PhysicsFormula.shortestFromPointToLineSegment(this.center, segment))
      .sortWith((a, b) => a.magnitude < b.magnitude)

    originToSegments.head
  }
}
