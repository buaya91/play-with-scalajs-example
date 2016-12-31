package client.domain

import monix.reactive.Observable
import shared.protocol.{GameRequest, GameState}

trait Predictor {
  def predictions(selfID: String, serverState: Observable[GameState], inputs: Observable[GameRequest]): Observable[GameState]
}
