package client.debug

import client.gameplay.infrastructure.CanvasRenderer
import shared.physics.{AABB, Vec2}

object DebugRenderer extends CanvasRenderer {
  override def drawAABB(ctx: CanvasCtx, aabb: AABB, scalingFactor: Vec2): Unit = {
    super.drawAABB(ctx, aabb, scalingFactor)
    (aabb, scalingFactor) match {
      case (AABB(ct, half), Vec2(xf, yf)) =>
        ctx.fillText(
          s"(${ct.x}, ${ct.y})",
          ct.x * xf + 10,
          ct.y * yf + 10
        )
    }
  }
}
