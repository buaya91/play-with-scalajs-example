package client.domain

import shared.protocol._

trait GameRenderer {
  def render(state: GameState, selfID: String)
}
