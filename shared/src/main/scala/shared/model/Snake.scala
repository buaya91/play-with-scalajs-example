package shared.model

import shared.defaultSpeed
import shared.physics.AABB
/**
  * @author limqingwei
  */
case class Snake(id: String, body: Seq[AABB], direction: Direction, distancePerStep: Double = defaultSpeed)
