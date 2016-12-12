package shared.model

import shared.defaultSpeed
import shared.physics._

case class Snake(id: String,
                 name: String,
                 body: Seq[AABB],
                 direction: Direction,
                 distancePerStep: Double = defaultSpeed)
