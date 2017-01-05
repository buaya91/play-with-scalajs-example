package debug

import akka.actor.{Actor, Props}
import game.ServerReconciler
import game.actors.NextFrame
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.{GameState, JoinGame, SequencedGameRequest}

import scala.collection.SortedMap

class GameStateActor extends Actor {
  type UserID         = String
  type FrameNo        = Int
  type IndexedInputs  = Map[UserID, IdentifiedGameInput]
  type BufferedInputs = SortedMap[FrameNo, IndexedInputs]

  override def receive: Receive = unitialized

  def unitialized: Receive = {
    case i @ IdentifiedGameInput(uid, j: JoinGame) =>
      val initState = GameLogic.step(GameState.init, Seq(i))
      context.become(active(initState, SortedMap.empty, SortedMap.empty))
  }

  def active(confirmedState: GameState, processedInputs: BufferedInputs, unprocessedInputs: BufferedInputs): Receive = {

    case input @ IdentifiedGameInput(uid, req) =>
      // for unsequence request, simply use the latest seq
      val frameNo = req match {
        case s: SequencedGameRequest => s.seqNo
        case _                       => confirmedState.seqNo
      }

      // add new request to buffer
      val updatedUnprocessedInputs = {
        val existingInput    = unprocessedInputs.getOrElse(frameNo, Map.empty)
        val updatedByFrameNo = existingInput + (uid -> input)
        unprocessedInputs.updated(frameNo, updatedByFrameNo)
      }

      context.become(active(confirmedState, processedInputs, updatedUnprocessedInputs))

    case NextFrame =>
      /**
        * 1. reconcile and produce new state
        * 2. check if any frame got all player's input
        * 3. drop all buffer before that frame, and update confirm state with tat
        * 4. send back new state with latest seqNo of each player
        */
      // ensure every frame have an entry in BufferedInputs, empty
      val addedNewFrame: BufferedInputs = {
        val latestFrameNo = processedInputs.lastOption.map(_._1).getOrElse(confirmedState.seqNo) + 1

        unprocessedInputs
          .get(latestFrameNo)
          .map(_ => unprocessedInputs)
          .getOrElse(unprocessedInputs + (latestFrameNo -> Map.empty))
      }

      val allInputs = ServerReconciler.mergeFrames(processedInputs, addedNewFrame)

      val frameNoWithAllUser = {
        val allUser = confirmedState.snakes.map(_.id).toSet
        processedInputs.collectFirst {
          case (no, inputs) if inputs.keySet == allUser => no
        }
      }

      val (newConfirmedState, inputsToUse) =
        frameNoWithAllUser
          .map(frameNo => {
            val (prev, next) = allInputs.span { case (fN, _) => fN <= frameNo }
            val nextState    = ServerReconciler.reapplyInputs(confirmedState, prev)

            (nextState, next)
          })
          .getOrElse((confirmedState, allInputs))

      val latestSpeculatedState = ServerReconciler.reapplyInputs(newConfirmedState, inputsToUse)

//      println(s"newState: $latestSpeculatedState")

      context.sender() ! latestSpeculatedState
      context.become(active(newConfirmedState, inputsToUse, SortedMap.empty))
  }
}

object GameStateActor {
  def props: Props = Props[GameStateActor]
}
