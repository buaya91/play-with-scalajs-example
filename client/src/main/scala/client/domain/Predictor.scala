package client.api

import shared.protocol.GameState

trait Predictor {
  def predictions(initState: GameState, selfID: String)
}
