package client.domain

import shared.protocol._

trait Renderer {
  def render(state: GameState, selfID: String)
}
