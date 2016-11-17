package infrastructure

import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}

object InputControl {
  def captureEvents(element: HTMLElement): Observable[KeyboardEvent] = {
    Observable.create(OverflowStrategy.Unbounded) { sync =>
      element.addEventListener[KeyboardEvent]("keydown", (ev: KeyboardEvent) => sync.onNext(ev), true)

      Cancelable(() => {
        sync.onComplete()
      })
    }
  }
}