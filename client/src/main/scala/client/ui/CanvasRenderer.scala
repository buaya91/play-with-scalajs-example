package client.ui

import org.scalajs.dom.{CanvasRenderingContext2D, window}
import shared.model.{Apple, Snake}
import shared.physics.{AABB, Vec2}
import shared.protocol._

trait CanvasRenderer extends GameRenderer {
  type CanvasCtx = CanvasRenderingContext2D
  val ctx: CanvasCtx

  // todo: scaling factor should be in the shared.package object
  def drawAABB(aabb: AABB, scalingFactor: Vec2): Unit = {
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

  def drawAABBRing(aABB: AABB, scalingFactor: Vec2) = {
    val oriStyle = ctx.fillStyle

    // ring is double the size of body
    val outerRingExpansion = 2.0
    val innerRingExpansion = 1.6 // ring is (2.0 - 1.6) / 2 in width

    // shift up/left
    val shiftForOuter  = (outerRingExpansion - 1) / 2
    val shiftForInner  = (innerRingExpansion - 1) / 2
    val centerForOuter = aABB.center - Vec2(shiftForOuter, shiftForOuter)
    val centerForInner = aABB.center - Vec2(shiftForInner, shiftForInner)

    ctx.fillStyle = "red"
    drawAABB(aABB.copy(halfExtents = aABB.halfExtents * outerRingExpansion, center = centerForOuter), scalingFactor)

    ctx.fillStyle = "black"
    drawAABB(aABB.copy(halfExtents = aABB.halfExtents * innerRingExpansion, center = centerForInner), scalingFactor)

    ctx.fillStyle = oriStyle
    drawAABB(aABB, scalingFactor)
  }

  def drawSnake(snake: Snake, scalingFactor: Vec2) = {
    if (snake.speedBuff.frameLeft > 0) {
      snake.body.foreach(aabb => drawAABBRing(aabb, scalingFactor))
    } else {
      snake.body.foreach(aabb => drawAABB(aabb, scalingFactor))
    }

//    snake.body.headOption.foreach {
//      case AABB(Vec2(x, y), _) =>
//        val textDisplacement = if (snake.direction == Down) 10 else -5
//
//        ctx.fillText(
//          snake.name,
//          (x * scalingFactor.x) + textDisplacement,
//          (y * scalingFactor.y) + textDisplacement
//        )
//    }
  }

  def drawApple(apple: Apple, scalingFactor: Vec2) = {
//    (apple.position, scalingFactor) match {
//      case (AABB(ct, half), Vec2(xf, yf)) =>
//        ctx.drawImage(
//          flameImg,
//          ct.x * xf,
//          ct.y * yf,
//          half.x * xf,
//          half.y * yf
//        )
//    }
    drawAABB(apple.position, scalingFactor)
  }

  override def render(state: GameState, selfID: String) = {
    val dpr                 = window.devicePixelRatio
    val (w, h)              = (ctx.canvas.width / dpr, ctx.canvas.height / dpr)
    val scalingFactor: Vec2 = Vec2(w / shared.terrainX, h / shared.terrainY)

    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)

    ctx.fillStyle = "#ee42f4" // slight pink for self
    state.snakes.find(_.id == selfID).foreach(s => drawSnake(s, scalingFactor))

    ctx.fillStyle = "#f4ee42" // yellow for enemies
    state.snakes.filterNot(_.id == selfID).foreach(s => drawSnake(s, scalingFactor))

    ctx.fillStyle = "blue"
    state.apples.foreach(drawApple(_, scalingFactor))
  }
}
