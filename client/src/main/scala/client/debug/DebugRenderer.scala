package client.debug

import client.infrastructure.CanvasRenderer
import org.scalajs.dom.CanvasRenderingContext2D
import shared.physics.{AABB, Vec2}

class DebugRenderer(val ctx: CanvasRenderingContext2D) extends CanvasRenderer {
  override def drawAABB(aabb: AABB, scalingFactor: Vec2): Unit = {
    super.drawAABB(aabb, scalingFactor)
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
