package game.actors

import akka.actor.{Actor, Props}
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.{GameRequest, GameState, JoinGame, SequencedGameRequest}

import scala.collection.SortedMap

class GameStateActor extends Actor {
  type StateInputPair = (GameState, Set[IdentifiedGameInput])
  type UserID = String
  type FrameNo = Int
  type IndexedInputs = Map[UserID, IdentifiedGameInput]
  type BufferedInputs = SortedMap[FrameNo, IndexedInputs]

  override def receive: Receive = unitialized

  def unitialized: Receive = {
    case i @ IdentifiedGameInput(uid, j: JoinGame) =>
      val initState = GameLogic.step(GameState.init, Seq(i))
      context.become(active(initState, SortedMap.empty, SortedMap.empty))
  }

  def active(confirmedState: GameState,
             processedInputs: BufferedInputs,
             unprocessedInputs: BufferedInputs): Receive = {

    case input @ IdentifiedGameInput(uid, req) =>
      // for unsequence request, simply use the latest seq
//      val frameNo = req match {
//        case s: SequencedGameRequest => s.seqNo
//        case _ => confirmedState.seqNo
//      }

      // todo, pending client changes
      val frameNo = confirmedState.seqNo + 1

      val updatedUnprocessedInputs = {
        val existingInput = unprocessedInputs.getOrElse(frameNo, Map.empty)
        val updatedByFrameNo = existingInput + (uid -> input)
        unprocessedInputs.updated(frameNo, updatedByFrameNo)
      }

      context.become(
        active(confirmedState, processedInputs, updatedUnprocessedInputs))

    case NextFrame =>
      /**
        * 1. reconcile and produce new state
        * 2. check if any frame got all player's input
        * 3. drop all buffer before that frame, and update confirm state with tat
        * 4. send back new state with latest seqNo of each player
        */
      val addedNewFrame: BufferedInputs = {
        val latestFrameNo = processedInputs.lastOption
            .map(_._1)
            .getOrElse(0) + 1
        unprocessedInputs
          .get(latestFrameNo)
          .map(_ => unprocessedInputs)
          .getOrElse(unprocessedInputs + (latestFrameNo -> Map.empty))
      }

      val allInputs = mergeFrames(processedInputs, addedNewFrame)

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
            val nextState = reapplyInputs(confirmedState, prev)

            (nextState, next)
          })
          .getOrElse((confirmedState, allInputs))

      val latestSpeculatedState = reapplyInputs(newConfirmedState, inputsToUse)

//      println(s"newState: $latestSpeculatedState")

      context.sender() ! latestSpeculatedState
      context.become(active(newConfirmedState, inputsToUse, SortedMap.empty))
  }

  def reapplyInputs(lastConfirmedState: GameState,
                    bufferedInputs: BufferedInputs): GameState = {
    bufferedInputs.foldLeft(lastConfirmedState) {
      case (lastState, (_, inputs)) =>
        GameLogic.step(lastState, inputs.values.toSeq)
    }
  }

  def mergeInputs(existed: IndexedInputs,
                  newlyAdded: IndexedInputs): IndexedInputs = {
    newlyAdded.headOption match {
      case Some((uid, i)) =>
        val updated =
          existed.get(uid).map(_ => existed).getOrElse(existed + (uid -> i))
        mergeInputs(updated, newlyAdded.tail)
      case None => existed
    }
  }

  def mergeFrames(existed: BufferedInputs,
                  newlyAdded: BufferedInputs): BufferedInputs = {
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

object GameStateActor {
  def props: Props = Props[GameStateActor]
}
