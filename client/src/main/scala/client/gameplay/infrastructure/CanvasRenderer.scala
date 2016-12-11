package client.gameplay.infrastructure

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, window}
import shared.model.{GameState, Snake}
import shared.physics.{AABB, Vec2}

trait CanvasRenderer extends Renderer[dom.CanvasRenderingContext2D] {
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

  // todo: print name also
  def drawSnake(ctx: canvasCtx, snake: Snake) = {
    val dpr = window.devicePixelRatio
    val (w, h) = (ctx.canvas.width / dpr, ctx.canvas.height / dpr)

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

object CanvasRenderer extends CanvasRenderer
