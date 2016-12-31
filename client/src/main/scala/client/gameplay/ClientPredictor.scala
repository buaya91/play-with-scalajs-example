package client.gameplay

import client.domain.Predictor
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.{GameRequest, GameState}

import scala.collection.SortedMap
import scala.scalajs.js.timers._
import monix.execution.Scheduler.Implicits.global
import scala.scalajs.js.Date
import shared._

object ClientPredictor extends Predictor {
  type FrameNo = Int

  // TODO: use map for constant time access by frame no
  private var predictedState       = SortedMap.empty[FrameNo, GameState]
  private var receivedState        = SortedMap.empty[FrameNo, GameState]
  private var lastCmd: GameRequest = _
  private var timer: SetTimeoutHandle = setTimeout(0) { () =>
    ()
  }

  // single loop
  def predict(selfID: String, emitPerLoop: GameState => _): Unit = {
    val start  = Date.now()
    val lastRc = receivedState.lastOption
    predictedState = lastRc.map { case (fn, _) => predictedState.dropWhile(_._1 <= fn) }.getOrElse(predictedState)
    predictedState.lastOption.map {
      case (n, st) => (n + 1, GameLogic.step(st, Seq(IdentifiedGameInput(selfID, lastCmd))).increaseSeqNo)
    }.foreach(pair => {
      emitPerLoop(pair._2)
      predictedState + pair
    })

    val end    = Date.now()
    val towait = millisNeededPerUpdate()

    timer = setTimeout(towait - (end - start)) {
      predict(selfID, emitPerLoop)
    }
  }

  override def predictions(selfID: String, serverState: Observable[GameState], inputs: Observable[GameRequest]) = {
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
