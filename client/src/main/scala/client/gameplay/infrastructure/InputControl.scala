package client.gameplay.infrastructure

import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}
import shared.model
import shared.protocol.{ChangeDirection, SequencedGameRequest, GameRequest, SpeedUp}

object InputControl {
  def captureEventsKeyCode(element: HTMLElement): Observable[Int] = {
    Observable.create(OverflowStrategy.Unbounded) { sync =>
      element.addEventListener[KeyboardEvent]("keydown", (ev: KeyboardEvent) => {
        sync.onNext(ev.keyCode)
      }, true)

      Cancelable(() => {
        sync.onComplete()
      })
    }
  }
}
