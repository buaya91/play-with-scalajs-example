package game

import shared.core.IdentifiedGameInput
import shared.protocol.{GameState, SequencedGameRequest}
import shared._

import scala.collection.SortedMap

case class ServerGameState(private val lastConfirmedState: GameState = GameState.init,
                           private val lastUnconfirmedFrameNo: Int = GameState.init.seqNo,
                           processedInputs: BufferedInputs = SortedMap.empty,
                           unprocessedInputs: BufferedInputs = SortedMap.empty,
                           toSend: BufferedInputs = SortedMap.empty) {

  def queueInput(input: IdentifiedGameInput): ServerGameState = {
    val frameNo = input.cmd match {
      case s: SequencedGameRequest => s.seqNo
      case _                       => lastUnconfirmedFrameNo + 1
    }

    println(s"Client $frameNo - Server: $lastUnconfirmedFrameNo - Diff ${frameNo - lastUnconfirmedFrameNo}")

    // add new request to buffer
    val updatedUnprocessedInputs = {
      val existingInput    = unprocessedInputs.getOrElse(frameNo, Map.empty)
      val updatedByFrameNo = existingInput + (input.playerID -> input)
      unprocessedInputs.updated(frameNo, updatedByFrameNo)
    }

    copy(unprocessedInputs = updatedUnprocessedInputs)
  }

  def nextState: ServerGameState = {

    val allInputs = ServerReconciler.mergeFrames(processedInputs, unprocessedInputs)

    val (toDrop: BufferedInputs, toKeep: BufferedInputs) = {
      allInputs.span {
        case (n, _) => n <= lastUnconfirmedFrameNo - serverBufferFrameSize
      }
    }

    val newConfirmedState: GameState =
      ServerReconciler.reapplyInputs(lastConfirmedState, toDrop, lastUnconfirmedFrameNo - serverBufferFrameSize)

    copy(lastConfirmedState = newConfirmedState,
         lastUnconfirmedFrameNo = lastUnconfirmedFrameNo + 1,
         processedInputs = toKeep,
         unprocessedInputs = SortedMap.empty,
         toSend = toDrop)
  }

  val predictedState: GameState = {
    val allInputs =
      ServerReconciler.mergeFrames(processedInputs, unprocessedInputs).dropWhile(_._1 <= lastConfirmedState.seqNo)

    ServerReconciler.reapplyInputs(lastConfirmedState, allInputs, lastUnconfirmedFrameNo)
  }
}
