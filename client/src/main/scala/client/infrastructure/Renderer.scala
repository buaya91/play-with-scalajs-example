package client.infrastructure

import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import shared.model.{GameState, Snake}
import shared.physics.AABB

trait Renderer[Context] {
  def render(ctx: Context, state: GameState)
}

object CanvasRenderer extends Renderer[dom.CanvasRenderingContext2D] {
  type canvasCtx = dom.CanvasRenderingContext2D

  // todo: scaling factor should be in the shared.package object
  def drawAABB(ctx: canvasCtx, aabb: AABB, scalingFactor: Int): Unit = {
    ctx.fillRect(aabb.center.x, aabb.center.y, aabb.halfExtents.x, aabb.halfExtents.y)
  }

  def drawSnake(ctx: canvasCtx, snake: Snake) = {
    snake.body.foreach(aabb => drawAABB(ctx, aabb, 10))
  }

  override def render(ctx: CanvasRenderingContext2D, state: GameState) = {
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)

    ctx.fillStyle = "white"
    state.snakes.foreach(drawSnake(ctx, _))
  }
}
