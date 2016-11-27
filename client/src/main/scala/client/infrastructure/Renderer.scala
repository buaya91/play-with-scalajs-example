package client.infrastructure

import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import shared.model.{GameState, Snake}
import shared.physics.{AABB, Vec2}

trait Renderer[Context] {
  def render(ctx: Context, state: GameState)
}

object CanvasRenderer extends Renderer[dom.CanvasRenderingContext2D] {
  type canvasCtx = dom.CanvasRenderingContext2D

  // todo: scaling factor should be in the shared.package object
  def drawAABB(ctx: canvasCtx, aabb: AABB, scalingFactor: Vec2): Unit = {
    (aabb, scalingFactor) match {
      case (AABB(ct, half), Vec2(xf, yf)) =>
        ctx.fillRect(
          ct.x * xf,
          ct.y * yf,
          half.x * xf,
          half.y * yf
        )
    }
  }

  def drawSnake(ctx: canvasCtx, snake: Snake) = {
    val (w, h) = (ctx.canvas.width, ctx.canvas.height)

    val scalingFactor: Vec2 = Vec2(w / shared.terrainX, h / shared.terrainY)

    snake.body.foreach(aabb => drawAABB(ctx, aabb, scalingFactor))
  }

  override def render(ctx: CanvasRenderingContext2D, state: GameState) = {
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)

    ctx.fillStyle = "white"
    state.snakes.foreach(drawSnake(ctx, _))
  }
}
