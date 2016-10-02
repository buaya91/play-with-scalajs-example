package infrastructure

import domain.GameWorld
import domain.components.Position
import domain.systems.RenderSystem
import org.scalajs.dom
import configs.Config._

class CanvasRenderSystem(ctx: dom.CanvasRenderingContext2D) extends RenderSystem {

  def drawPoint(position: Position, scalingFactor: Double = 10): Unit = {
    ctx.fillRect(position.x * scalingFactor, position.y * scalingFactor, scalingFactor, scalingFactor)
  }

  def renderScore(snake: Seq[Position]): Unit = {
    val width = ctx.canvas.width
    val fontSize = width / 50
    ctx.font = s"${fontSize}px sans-serif"
    ctx.fillStyle = "white"

    val score = snake.size
    val scoreTxt = s"Score: $score"
    ctx.fillText(scoreTxt, 10, fontSize)
  }

  override def process(world: GameWorld): Unit = {
    val areaComponents = world.areaComponents
    val isSnakeComponent = world.isSnakeComponents

    val width = ctx.canvas.width
    val height = ctx.canvas.height

    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, width, height)

    val scaleFactor = (width / gameX) / 2       // TODO: need consider HPI

    ctx.fillStyle = "white"   // debug color

    areaComponents.foreach {
      case (id, area) =>

        val isSnake = isSnakeComponent.getOrElse(id, false)

        if (isSnake) {
          ctx.fillStyle = area.size match {
            case x if x > 30 => "#ffff99"
            case x if x > 20 => "#0099cc"
            case x if x > 10 => "#00cc00"
            case _           => "green"
          }

          area.foreach(drawPoint(_, scaleFactor))
        } else {
          ctx.fillStyle = "#ff0066"
          area.foreach(drawPoint(_, scaleFactor))
        }

//        if (i == id)
//          renderScore(a)
    }
  }
}
