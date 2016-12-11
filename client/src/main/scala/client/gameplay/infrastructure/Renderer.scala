package client.gameplay.infrastructure

import org.scalajs.dom
import org.scalajs.dom.window
import org.scalajs.dom.CanvasRenderingContext2D
import shared.model.{GameState, Snake}
import shared.physics.{AABB, Vec2}

trait Renderer[Context] {
  def render(ctx: Context, state: GameState)
}
