package client.refactor

import monix.execution.{Ack, Cancelable}
import monix.reactive.{Observable, OverflowStrategy}
import shared.{FrameNo, millisNeededPerUpdate}
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.protocol.{GameState, SequencedGameRequest}

import scala.collection.SortedMap
import scala.scalajs.js.Date
import org.scalajs.dom.window
import scala.scalajs.js.timers.{SetTimeoutHandle, clearTimeout, setTimeout}

object GameLoop {
  import GlobalData._

  private var timer: Int = 0
  private var frameEndedAt: Double = 0

  private def step() = {
    val nextStep = for {
      id                 <- assignedID
      _                  <- userName
      (lastN, lastState) <- (predictedState ++ serverStateQueue).lastOption
    } yield {

      val conflicted = for {
        (lastServerN, latestServerState) <- serverStateQueue.lastOption
        correspondPrediction             <- predictedState.get(lastServerN)
        if correspondPrediction != latestServerState
      } yield {
        (lastServerN to lastN + 1).foldLeft(latestServerState) {
          case (st, _) =>
            val inputs = unackInput.get(st.seqNo + 1).map(IdentifiedGameInput(id, _)).toSeq
            val next   = GameLogic.step(st, inputs)
            next
        }
      }

      conflicted.getOrElse {
        val nextInput = unackInput.get(lastN + 1).map(i => Seq(IdentifiedGameInput(id, i))).getOrElse(Seq())
        GameLogic.step(lastState, nextInput)
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
    timer = window.requestAnimationFrame { (_: Double) =>
      val now = Date.now()

      if (frameEndedAt == 0) {
        step()
        (predictedState ++ serverStateQueue).lastOption.foreach(pair => push(pair._2))
        frameEndedAt = Date.now()
      } else {
        val sinceLastFrame = now - frameEndedAt
        val framePast      = sinceLastFrame / millisNeededPerUpdate

        if (framePast >= 1) {
          (1 to framePast.toInt).foreach(_ => step())
          (predictedState ++ serverStateQueue).lastOption.foreach(pair => push(pair._2))
          frameEndedAt = Date.now()
        }
      }

      loop(push)
    }
  }

  def start(): Observable[GameState] = {
    Observable.create(OverflowStrategy.Unbounded) { sync =>
      loop(sync.onNext)

      Cancelable(() => {
        window.cancelAnimationFrame(timer)
        sync.onComplete()
      })
    }
  }
}
