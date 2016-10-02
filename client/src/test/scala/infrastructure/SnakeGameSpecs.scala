package infrastructure

import api.SnakeApi
import org.scalatest.{Matchers, WordSpec}
import configs.Config._
import domain.GameWorld
import domain.components.Direction

class SnakeGameSpecs extends WordSpec with Matchers {
  "SnakeGame.build" should {
    "create snake that fit in game size" in {

      val game = new SnakeApi {
        override val world: GameWorld = new GameWorld()

        override def changeDir(id: String, dir: Direction): Unit = {}

        override def speedUp(id: String): Unit = {}
      }

      game.addNewSnake("test")

      val area = game.world.areaComponents("test")

      area.foreach(p => {
        p.x should (be >= 0 and be <= gameX)
        p.y should (be >= 0 and be <= gameY)
      })
    }
  }
}
