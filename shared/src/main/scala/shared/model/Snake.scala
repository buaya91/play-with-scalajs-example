package shared.model

import shared.defaultSpeed
/**
  * @author limqingwei
  */
case class Snake(id: String, body: Seq[Position], direction: Direction, distancePerStep: Double = defaultSpeed)
