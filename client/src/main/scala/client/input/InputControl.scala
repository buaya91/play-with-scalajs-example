package client.input

import monix.reactive.Observable

trait InputControl {
  def captureInputs(): Observable[Int]
}
