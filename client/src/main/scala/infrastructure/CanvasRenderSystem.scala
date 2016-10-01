package infrastructure

import domain.GameWorld
import domain.components.Position
import domain.systems.RenderSystem
import org.scalajs.dom

class CanvasRenderSystem(ctx: dom.CanvasRenderingContext2D) extends RenderSystem {

  def drawPoint(position: Position, scalingFactor: Int = 10): Unit = {
    ctx.fillRect(position.x * scalingFactor, position.y * scalingFactor,scalingFactor, scalingFactor)
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

    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)

    areaComponents.foreach {
      case (i, a) =>
        val isSnake = isSnakeComponent.getOrElse(i, false)
        if (isSnake) {
          ctx.fillStyle = a.size match {
            case x if x > 30 => "#ffff99"
            case x if x > 20 => "#0099cc"
            case x if x > 10 => "#00cc00"
            case _           => "green"
          }

          a.foreach(drawPoint(_))
        } else {
          ctx.fillStyle = "#ff0066"
          a.foreach(drawPoint(_))
        }

//        if (i == id)
//          renderScore(a)
    }
  }
}
