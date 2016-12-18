package client.gameplay.infrastructure

import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}
import shared.model
import shared.protocol.{ChangeDirection, SequencedGameRequest, GameRequest, SpeedUp}

object InputControl {
  def captureEvents(element: HTMLElement): Observable[GameRequest] = {
    Observable.create(OverflowStrategy.Unbounded) { sync =>
      element.addEventListener[KeyboardEvent]("keydown", (ev: KeyboardEvent) => {
        val keyToCmd: PartialFunction[Int, SequencedGameRequest] = {
          case 32 => SpeedUp()
          case 37 => ChangeDirection(model.Left)
          case 38 => ChangeDirection(model.Up)
          case 39 => ChangeDirection(model.Right)
          case 40 => ChangeDirection(model.Down)
        }

        if (keyToCmd.isDefinedAt(ev.keyCode))
          sync.onNext(keyToCmd(ev.keyCode))

      }, true)

      Cancelable(() => {
        sync.onComplete()
      })
    }
  }
}
