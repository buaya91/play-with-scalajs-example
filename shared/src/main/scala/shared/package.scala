import shared.model._
import shared.physics.Vec2

package object shared {
  val terrainX        = 100
  val terrainY        = 75
  lazy val terrainVec = Vec2(terrainX, terrainY)

  val snakeBodyUnitSize        = Vec2(1, 1)
  val snakeBodyInitLength      = 5
  val distancePerSec: Double   = 20.0
  val areaOccupiedPerPosition  = 1 * 1
  val fps                      = 50
  val serverBufferFrameSize    = 2 * fps // 2 seconds
  val millisNeededPerUpdate    = 1000 / fps
  val distancePerFrame: Double = distancePerSec / fps

  val rangeFactor = 1.0 // to control sensitivity of collision

  def unitPerDirection(dir: Direction): Vec2 = dir match {
    case Up          => Vec2(0, -1)
    case Down        => Vec2(0, 1)
    case model.Right => Vec2(1, 0)
    case model.Left  => Vec2(-1, 0)
  }
}
