package client.input

import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}

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
