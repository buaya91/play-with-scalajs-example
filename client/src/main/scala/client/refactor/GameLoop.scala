package client.refactor

import monix.execution.{Ack, Cancelable}
import monix.reactive.{Observable, OverflowStrategy}
import shared.{FrameNo, millisNeededPerUpdate}
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.{GameState, JoinGame, SequencedGameRequest}
import shared._

import scala.collection.SortedMap
import scala.scalajs.js.Date
import scala.scalajs.js.timers.{SetTimeoutHandle, clearTimeout, setTimeout}

class GameLoop(data: MutableGameData) {
  import data._
  private var timer: SetTimeoutHandle = setTimeout(0) {}

  private def stepWithDelta(): Unit = {

    /**
      * 1. if server state ++ predicted is not empty
      * 2. pick the latest
      * 3. check delta queue, if not empty
      * 1. reconcile over existing predictions
      * 2. drop the predictions < last input
      * 3. cut predictions to be smaller than buffer size
      * 4. one last step
      */
    for {
      id <- assignedID
      allStates = predictedState ++ serverStateQueue
      if allStates.nonEmpty
    } yield {
      if (unackDelta.nonEmpty) {
        unackDelta.values.foreach { delta =>
          delta.inputs.foreach {
            case IdentifiedGameInput(_, JoinGame(n)) =>
              println(s"Got Join ${delta.seqNo}")
              println(s"Start: ${allStates.head._1}")
            case _ =>
          }
        }

        val adjustedPredictions = allStates.scanLeft(allStates.head) {
          case ((_, st), _) =>
            val serverInputs = unackDelta.get(st.seqNo + 1).toSeq.flatMap(_.inputs)
            val clientInputs = unackInput.get(st.seqNo + 1).map(IdentifiedGameInput(id, _)).toSeq
            val next         = GameLogic.step(st, serverInputs ++ clientInputs)

            next._1.seqNo -> next._1
        }
        predictedState = adjustedPredictions
      }

      val (lastN, lastState) = allStates.last
      val nextInput = unackInput.get(lastN + 1).map(i => Seq(IdentifiedGameInput(id, i))).getOrElse(Seq())
      val nextState = GameLogic.step(lastState, nextInput)._1

      predictedState = (predictedState + (nextState.seqNo -> nextState)).takeRight(serverBufferFrameSize)
      unackInput = unackInput.takeRight(serverBufferFrameSize)
      unackDelta = SortedMap.empty
      serverStateQueue = SortedMap.empty
    }
  }

  private def step() = {
    val nextStep = for {
      id                 <- assignedID
      (lastN, lastState) <- (predictedState ++ serverStateQueue).lastOption
    } yield {

      val conflictResolved = for {
        (lastServerN, latestServerState) <- serverStateQueue.lastOption
        correspondPrediction             <- predictedState.get(lastServerN)
        if correspondPrediction != latestServerState
      } yield {
        (lastServerN to lastN + 1).foldLeft(latestServerState) {
          case (st, _) =>
            val inputs = unackInput.get(st.seqNo + 1).map(IdentifiedGameInput(id, _)).toSeq
            val next   = GameLogic.step(st, inputs)._1
            next
        }
      }

      conflictResolved.getOrElse {
        val nextInput = unackInput.get(lastN + 1).map(i => Seq(IdentifiedGameInput(id, i))).getOrElse(Seq())
        GameLogic.step(lastState, nextInput)._1
      }
    }

    serverStateQueue.lastOption.foreach {
      case (n, _) =>
        predictedState = predictedState.dropWhile(_._1 < n)
        unackInput = unackInput.dropWhile(_._1 < n)
        serverStateQueue = SortedMap.empty
    }

    nextStep.foreach(st => predictedState = predictedState + (st.seqNo -> st))
  }

  private def loop(push: GameState => Ack): Unit = {
    val startT = Date.now()
    stepWithDelta()
    (predictedState ++ serverStateQueue).lastOption.foreach(pair => push(pair._2))
    val timeTaken = Date.now() - startT

    val toWait = Math.max(0, millisNeededPerUpdate - timeTaken)

    timer = setTimeout(toWait) {
      loop(push)
    }
  }

  def start(): Observable[GameState] = {
    Observable.create(OverflowStrategy.Unbounded) { sync =>
      loop(sync.onNext)

      Cancelable(() => {
        clearTimeout(timer)
        sync.onComplete()
      })
    }
  }
}
