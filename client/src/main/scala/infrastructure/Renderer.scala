package infrastructure

import domain._
import org.scalajs.dom

trait Renderer[Context] {
  def render(ctx: Context, world: GameWorld)
}

object CanvasRenderer extends Renderer[dom.CanvasRenderingContext2D] {
  type canvasCtx = dom.CanvasRenderingContext2D
  override def render(ctx: canvasCtx, world: GameWorld): Unit = {
    if (world.snakes.isEmpty) {
      val txt =
        """
          |Game Over!
          |Press any key to continue
        """.stripMargin
      ctx.fillStyle = "white"
      val fontSize = ctx.canvas.width / 40
      ctx.font = s"${fontSize}px sans-serif"
      ctx.textAlign = "center"
      ctx.textBaseline = "middle"

      val w = dom.window.innerWidth / 2
      val h = dom.window.innerHeight / 2

      ctx.fillText(txt,  w, h)

    } else {
      ctx.fillStyle = "black"
      ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)

      val snacks = world.snacks
      val snakes = world.snakes

      snakes.foreach(renderSnake(ctx, _))
      snacks.foreach(renderSnack(ctx, _))
      renderScore(ctx, snakes.head)
    }
  }

  def drawPoint(ctx: canvasCtx, position: Position, scalingFactor: Int = 10): Unit = {
    ctx.fillRect(position.x * scalingFactor, position.y * scalingFactor,scalingFactor, scalingFactor)
  }

  def renderSnake(ctx: canvasCtx, snake: Snake): Unit = {
    ctx.fillStyle = snake.body.size match {
      case x if x > 30 => "#ffff99"
      case x if x > 20 => "#0099cc"
      case x if x > 10 => "#00cc00"
      case _           => "green"
    }

    snake.body.foreach(drawPoint(ctx, _))
  }

  def renderSnack(ctx: canvasCtx, snacks: Snacks): Unit = {
    snacks match {
      case Chocolate(p) =>
        ctx.fillStyle = "#ff0066"
        drawPoint(ctx, p)
    }
  }

  def renderScore(ctx: canvasCtx, snake: Snake): Unit = {
    val width = ctx.canvas.width
    val fontSize = width / 50
    ctx.font = s"${fontSize}px sans-serif"
    ctx.fillStyle = "white"

    val score = snake.body.size
    val scoreTxt = s"Score: $score"
    ctx.fillText(scoreTxt, 10, fontSize)
  }
}