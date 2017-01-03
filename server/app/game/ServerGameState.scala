package game

import shared.core.IdentifiedGameInput
import shared.protocol.{GameState, SequencedGameRequest}

import scala.collection.SortedMap

case class ServerGameState(private val lastConfirmedState: GameState = GameState.init,
                           processedInputs: BufferedInputs = SortedMap.empty,
                           unprocessedInputs: BufferedInputs = SortedMap.empty) {

  def queueInput(input: IdentifiedGameInput): ServerGameState = {
    val frameNo = input.cmd match {
      case s: SequencedGameRequest => s.seqNo
      case _                       => lastConfirmedState.seqNo
    }

    // add new request to buffer
    val updatedUnprocessedInputs = {
      val existingInput    = unprocessedInputs.getOrElse(frameNo, Map.empty)
      val updatedByFrameNo = existingInput + (input.playerID -> input)
      unprocessedInputs.updated(frameNo, updatedByFrameNo)
    }

    copy(unprocessedInputs = updatedUnprocessedInputs)
  }

  def nextState: ServerGameState = {

    // add new frame into input buffer
    val addedNewFrame: BufferedInputs = {
      val latestFrameNo = processedInputs.lastOption.map(_._1).getOrElse(lastConfirmedState.seqNo) + 1

      unprocessedInputs
        .get(latestFrameNo)
        .map(_ => unprocessedInputs)
        .getOrElse(unprocessedInputs + (latestFrameNo -> Map.empty))
    }

    val allInputs = ServerReconciler.mergeFrames(processedInputs, addedNewFrame)

    val frameNoWithAllUser: Option[FrameNo] = {
      val allUser = lastConfirmedState.snakes.map(_.id).toSet
      processedInputs.collectFirst {
        case (no, inputs) if inputs.keySet == allUser => no
      }
    }

    val newConfirmedState: GameState =
      frameNoWithAllUser
        .map(frameNo => {
          val (prev, _) = allInputs.span { case (fN, _) => fN <= frameNo }
          val nextState = ServerReconciler.reapplyInputs(lastConfirmedState, prev)

          nextState
        })
        .getOrElse(lastConfirmedState)

    copy(lastConfirmedState = newConfirmedState)
  }

  lazy val predictedState: GameState = {
    val allInputs = ServerReconciler.mergeFrames(processedInputs, unprocessedInputs)
    ServerReconciler.reapplyInputs(lastConfirmedState, allInputs)
  }
}
