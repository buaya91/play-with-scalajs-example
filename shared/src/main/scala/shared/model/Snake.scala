package shared.model

import shared.physics._

case class Snake(id: String,
                 name: String,
                 body: Seq[AABB],
                 direction: Direction,
                 distancePerStep: Double,
                 energy: Int = 0)
