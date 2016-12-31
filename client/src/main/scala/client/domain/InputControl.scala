package client.domain

import monix.reactive.Observable
import shared.protocol.SequencedGameRequest

trait InputControl {
  def captureEventsKeyCode: Observable[Int => SequencedGameRequest]
}
