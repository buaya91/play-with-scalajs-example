package client.gameplay.infrastructure

import shared.protocol._

trait Renderer[Context] {
  def render(ctx: Context, state: GameState, selfID: String)
}
