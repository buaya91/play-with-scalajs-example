package client.domain

import monix.reactive.Observable
import shared.protocol.SequencedGameRequest

trait InputControl {
  def captureInputs(): Observable[Int]
}
