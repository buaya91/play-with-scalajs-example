package client.infrastructure

import client.domain.InputControl
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}
import shared.model
import shared.protocol.{ChangeDirection, SequencedGameRequest, SpeedUp}

class KeyboardInput(element: HTMLElement) extends InputControl {

  private val keyAllowed = Set(32, 37, 38, 39, 40)

  def captureInputs(): Observable[Int] = {
    Observable.create(OverflowStrategy.Unbounded) { sync =>
      element.addEventListener[KeyboardEvent]("keydown", (ev: KeyboardEvent) => {
        if (keyAllowed.contains(ev.keyCode)) {
          sync.onNext(ev.keyCode)
        }
      })

      Cancelable(() => {
        sync.onComplete()
      })
    }
  }
}
