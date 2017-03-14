package game

import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.GameState

import scala.collection.SortedMap

object ServerReconciler {
  type IndexedInputs  = Map[String, IdentifiedGameInput]
  type BufferedInputs = SortedMap[Int, IndexedInputs]

  def reapplyInputs(from: GameState, bufferedInputs: BufferedInputs, end: Int): GameState = {
    val state = (from.seqNo to end).foldLeft(from) {
      case (st, _) =>
        val inputs = bufferedInputs.getOrElse(st.seqNo + 1, Map.empty).values.toSeq
        val next = GameLogic.step(st, inputs)._1
        next
    }

    state
  }

  private def mergeInputs(existed: IndexedInputs, newlyAdded: IndexedInputs): IndexedInputs = {
    newlyAdded.headOption match {
      case Some((uid, i)) =>
        val updated =
          existed.get(uid).map(_ => existed).getOrElse(existed + (uid -> i))
        mergeInputs(updated, newlyAdded.tail)
      case None => existed
    }
  }

  def mergeFrames(existed: BufferedInputs, newlyAdded: BufferedInputs): BufferedInputs = {
    newlyAdded.headOption match {
      case Some(pair @ (fn, inputs)) =>
        val updated: BufferedInputs = existed
          .get(fn)
          .map(frame => {
            val x = mergeInputs(frame, inputs)
            existed + (fn -> x)
          })
          .getOrElse(existed + pair)

        mergeFrames(updated, newlyAdded.tail)
      case None => existed
    }
  }

}
