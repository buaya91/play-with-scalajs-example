package game

import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.GameState

import scala.collection.SortedMap

object ServerReconciler {
  type IndexedInputs  = Map[String, IdentifiedGameInput]
  type BufferedInputs = SortedMap[Int, IndexedInputs]

  def reapplyInputs(lastConfirmedState: GameState, bufferedInputs: BufferedInputs): GameState = {
    bufferedInputs.foldLeft(lastConfirmedState) {
      case (lastState, (_, inputs)) =>
        GameLogic.step(lastState, inputs.values.toSeq)
    }
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
