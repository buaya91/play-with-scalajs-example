package client.gameplay.infrastructure

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, window}
import shared.model.{Down, Snake}
import shared.protocol._
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

  def drawSnake(ctx: canvasCtx, snake: Snake) = {
    val dpr = window.devicePixelRatio
    val (w, h) = (ctx.canvas.width / dpr, ctx.canvas.height / dpr)

    val scalingFactor: Vec2 = Vec2(w / shared.terrainX, h / shared.terrainY)

    snake.body.foreach(aabb => drawAABB(ctx, aabb, scalingFactor))

    snake.body.headOption.foreach {
      case AABB(Vec2(x, y), _) =>
        val textDisplacement = if (snake.direction == Down) 10 else -5

        ctx.fillText(
          snake.name,
          (x * scalingFactor.x) + textDisplacement,
          (y * scalingFactor.y) + textDisplacement
        )
    }
  }

  override def render(ctx: CanvasRenderingContext2D, state: GameState, selfID: String) = {
    ctx.fillStyle = "#c2d6d6"   // grey for background
    ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)

    ctx.fillStyle = "#ff5050"   // slight pink for self
    state.snakes.find(_.id == selfID).foreach(s => drawSnake(ctx, s))

    ctx.fillStyle = "yellow"   // yellow for enemies
    state.snakes.filterNot(_.id == selfID).foreach(s => drawSnake(ctx, s))
  }
}

object CanvasRenderer extends CanvasRenderer
