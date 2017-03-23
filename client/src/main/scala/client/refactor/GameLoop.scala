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
  private var timer: SetTimeoutHandle   = setTimeout(0) {}
  private var expectedNextFrame: Double = _

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
      val adjustedPredictions = if (unackDelta.nonEmpty) {
//        unackDelta.lastOption.foreach { delta =>
//          delta._2.inputs.foreach { _ =>
//            println(s"Diff: ${delta._1 - allStates.keys.head}")
//          }
//        }

        var newPredictions = SortedMap.empty[FrameNo, GameState]

        for {
          (seqNo, st) <- allStates
        } yield {
          val serverInputs = unackDelta.get(seqNo + 1).toSeq.flatMap(_.inputs)
          val clientInputs = unackInput.get(seqNo + 1).map(IdentifiedGameInput(id, _)).toSeq
          val next         = GameLogic.applyInput(st, serverInputs ++ clientInputs)
          newPredictions += next.seqNo -> next
        }

        newPredictions
      } else predictedState

      val (lastN, lastState) = (adjustedPredictions ++ serverStateQueue).last
      val nextInput          = unackInput.get(lastN + 1).map(i => Seq(IdentifiedGameInput(id, i))).getOrElse(Seq())
      val nextState          = GameLogic.step(lastState, nextInput)._1

      // TODO: problem client input start drifting
      predictedState = (adjustedPredictions + (nextState.seqNo -> nextState)).takeRight(serverBufferFrameSize)
      unackInput = unackInput.dropWhile(_._1 < predictedState.head._1)
      unackDelta = unackDelta.dropWhile(_._1 < predictedState.head._1)
//      unackDelta = SortedMap.empty
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
    val delay  = startT - expectedNextFrame
    if (delay >= 5) println(s"Delay: $delay")
    stepWithDelta()
    (predictedState ++ serverStateQueue).lastOption.foreach(pair => push(pair._2))
    val timeTaken = Date.now() - startT

    val toWait = millisNeededPerUpdate - timeTaken - delay

    expectedNextFrame += millisNeededPerUpdate

    timer = setTimeout(toWait) {
      loop(push)
    }
  }

  def start(): Observable[GameState] = {
    Observable.create(OverflowStrategy.Unbounded) { sync =>
      expectedNextFrame = Date.now()
      loop(sync.onNext)

      Cancelable(() => {
        clearTimeout(timer)
        sync.onComplete()
      })
    }
  }
}
