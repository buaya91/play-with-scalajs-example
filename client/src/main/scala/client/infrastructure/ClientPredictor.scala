package client.infrastructure

import client.domain.Predictor
import monix.execution.{Cancelable, Scheduler}
import monix.reactive.{Observable, OverflowStrategy}
import shared._
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.{GameRequest, GameState, NoOp}

import scala.collection.SortedMap
import scala.scalajs.js.Date
import scala.scalajs.js.timers._

object ClientPredictor extends Predictor {
  type FrameNo = Int

  // TODO: use map for constant time access by frame no
  private var predictedState       = SortedMap.empty[FrameNo, GameState]
  private var receivedState        = SortedMap.empty[FrameNo, GameState]
  private var lastCmd: GameRequest = NoOp(0)
  private var timer: SetTimeoutHandle = setTimeout(0) { () =>
    ()
  }

  private def step(lastFrame: (Int, GameState), selfID: String): (Int, GameState) = {
    val latestInput = Seq(IdentifiedGameInput(selfID, lastCmd))
    val newState    = GameLogic.step(lastFrame._2, latestInput).increaseSeqNo
    lastFrame._1 + 1 -> newState
  }

  // single loop
  private def predict(selfID: String, emitPerLoop: GameState => _): Unit = {
    val start  = Date.now()
    val lastRc = receivedState.lastOption

    predictedState = lastRc.map { case (fn, _) => predictedState.dropWhile(_._1 <= fn) }.getOrElse(predictedState)

    val lastPredicted = predictedState.lastOption

    val latestState = (lastPredicted, lastRc) match {
      case (Some(p), _)    => Some(p)
      case (None, Some(s)) => Some(s)
      case _ =>
        println("nothing to base on")
        None
    }

    latestState.foreach(frame => {
      val nextFrame = step(frame, selfID)
      emitPerLoop(nextFrame._2)
      predictedState += nextFrame
    })

    val end    = Date.now()
    val toWait = Math.max(0, millisNeededPerUpdate() - (end - start))

    timer = setTimeout(toWait) {
      predict(selfID, emitPerLoop)
    }
  }

  override def predictions(selfID: String,
                           serverState: Observable[GameState],
                           inputs: Observable[GameRequest])(implicit scheduler: Scheduler): Observable[GameState] = {
    serverState.foreach(st => receivedState = receivedState + (st.seqNo -> st))
    inputs.foreach(req => lastCmd = req)

    Observable.create(OverflowStrategy.Unbounded) { sync =>
      predict(selfID, sync.onNext)

      Cancelable(() => {
        clearTimeout(timer)
        sync.onComplete()
      })
    }
  }
}
