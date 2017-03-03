package ai

import shared._
import shared.model.Snake
import shared.physics.{AABB, Vec2}
import shared.protocol.{GameState, SequencedGameRequest}

trait SnakeAI {
  val id: String

  private def findClosestAABB(a: AABB, searchSpace: Seq[AABB]): AABB = {
    searchSpace.foldLeft(searchSpace.head) {
      case (closest, next) =>
        val d1 = (a.center - closest.center).magnitude
        val d2 = (a.center - next.center).magnitude

        if (d1 < d2)
          closest
        else
          next
    }
  }

  def findNearestEnemy(self: Snake, state: GameState): Option[Snake] = {
    val selfHead = self.body.headOption

    selfHead.flatMap { s =>
      val quaterRadiusPoint = AABB(s.halfExtents, s.center + Vec2(terrainX / 4, terrainY / 4))

      val closest = state.snakes.foldLeft[(AABB, Option[Snake])](quaterRadiusPoint -> None) {
        case ((closestAcc, closestSnk), snk) =>
          val closestNext = findClosestAABB(s, snk.body)
          val d1 = (closestAcc.center - s.center).magnitude
          val d2 = (closestNext.center - s.center).magnitude

          if (d1 < d2)
            closestAcc -> closestSnk
          else
            closestNext -> Some(snk)
      }
      closest._2
    }
  }

  def react(latestState: GameState): Option[SequencedGameRequest]
}
