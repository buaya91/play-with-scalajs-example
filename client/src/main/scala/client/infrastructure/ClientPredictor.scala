package client.infrastructure

import client.domain.Predictor
import monix.execution.Ack.Continue
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.{Observable, OverflowStrategy}
import shared._
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.{GameRequest, GameState, SequencedGameRequest}

import scala.collection.SortedMap
import scala.scalajs.js.Date
import scala.scalajs.js.timers._

object ClientPredictor extends Predictor {
  type FrameNo = Int

  // TODO: use map for constant time access by frame no
  private var predictedState = SortedMap.empty[FrameNo, GameState]
  private var receivedState  = SortedMap.empty[FrameNo, GameState]
  private var lastCmd        = SortedMap.empty[FrameNo, SequencedGameRequest]
  private var timer: SetTimeoutHandle = setTimeout(0) { () =>
    ()
  }

  private def step(lastFrame: (Int, GameState), selfID: String): (Int, GameState) = {
    val latestInput = lastCmd.map {
      case (_, req) => IdentifiedGameInput(selfID, req)
    }.toSeq
    val newState = GameLogic.step(lastFrame._2, latestInput).increaseSeqNo
    lastFrame._1 + 1 -> newState
  }

  // single loop
  private def predict(selfID: String, emitPerLoop: GameState => _): Unit = {
    val start  = Date.now()
    val lastRc = receivedState.lastOption

    predictedState = lastRc.map { case (fn, _) => predictedState.dropWhile(_._1 <= fn) }.getOrElse(predictedState)

    val lastPredicted = predictedState.lastOption

    val latestState = (lastPredicted, lastRc) match {
      case (_, Some(s)) => Some(s)
      case (Some(p), _) => Some(p)
      case _            => None
    }

    latestState.foreach(frame => {
      val nextFrame = step(frame, selfID)
      emitPerLoop(nextFrame._2)
      predictedState += nextFrame
    })

    val end    = Date.now()
    val toWait = Math.max(0, millisNeededPerUpdate - (end - start))

    timer = setTimeout(toWait) {
      predict(selfID, emitPerLoop)
    }
  }

  override def predictions(selfID: String, serverState: Observable[GameState], inputs: Observable[GameRequest])(
      implicit scheduler: Scheduler): Observable[GameState] = {
    serverState.subscribe(st => {
      receivedState = receivedState + (st.seqNo -> st)
      lastCmd = lastCmd.dropWhile {
        case (fn, _) => fn < st.seqNo
      }
      Continue
    })

    inputs.subscribe(req => {
      req match {
        case r: SequencedGameRequest => lastCmd = lastCmd + (r.seqNo -> r)
        case _                       =>
      }

      Continue
    })

    Observable.create(OverflowStrategy.Unbounded) { sync =>
      predict(selfID, sync.onNext)

      Cancelable(() => {
        clearTimeout(timer)
        sync.onComplete()
      })
    }
  }
}
