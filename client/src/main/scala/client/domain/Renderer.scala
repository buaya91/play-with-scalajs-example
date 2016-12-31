package client.api

import shared.protocol._

trait Renderer {
  def render(state: GameState, selfID: String)
}
