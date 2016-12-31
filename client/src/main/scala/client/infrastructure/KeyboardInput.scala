package client.infrastructure

import client.domain.InputControl
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}
import shared.model
import shared.protocol.{ChangeDirection, SequencedGameRequest, SpeedUp}

class KeyboardInput(element: HTMLElement) extends InputControl {

  val keyToCmd: PartialFunction[Int, Int => SequencedGameRequest] = {
    case 32 => SpeedUp.apply
    case 37 => ChangeDirection(model.Left, _)
    case 38 => ChangeDirection(model.Up, _)
    case 39 => ChangeDirection(model.Right, _)
    case 40 => ChangeDirection(model.Down, _)
  }

  def captureEventsKeyCode: Observable[Int => SequencedGameRequest] = {
    Observable.create(OverflowStrategy.Unbounded) { sync =>
      element.addEventListener[KeyboardEvent]("keydown", (ev: KeyboardEvent) => {
        if (keyToCmd.isDefinedAt(ev.keyCode)) {
          sync.onNext(keyToCmd(ev.keyCode))
        }
      }, true)

      Cancelable(() => {
        sync.onComplete()
      })
    }
  }
}
