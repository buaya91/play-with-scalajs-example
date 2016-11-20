import shared.model._
import shared.physics.Vec2

package object shared {
  val terrainX = 100
  val terrainY = 100

  val defaultSpeed = 0.02
  
  val areaOccupiedPerPosition = 1 * 1

  val updateRate = 30
  val millisNeededPerUpdate = 1000 / updateRate

  def unitPerDirection(dir: Direction): Vec2 = dir match {
    case Up =>  Vec2(0, 1)
    case Down => Vec2(0, -1)
    case model.Right => Vec2(1, 0)
    case model.Left =>Vec2(-1, 0)
  }
}
