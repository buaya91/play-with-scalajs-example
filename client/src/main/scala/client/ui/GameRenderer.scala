package client.ui

import shared.protocol._

trait GameRenderer {
  def render(state: GameState, selfID: String)
}
