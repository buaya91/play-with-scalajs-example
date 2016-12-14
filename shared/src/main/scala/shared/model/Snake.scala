package shared.model

import shared.physics._

case class Snake(id: String,
                 name: String,
                 body: Seq[AABB],
                 direction: Direction,
                 speedBuff: SpeedBuff = SpeedBuff(0),
                 energy: Int = 0)
