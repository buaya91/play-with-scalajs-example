package domain.systems

import api.SnakeApi
import domain.GameWorld
import domain.components._
import org.scalatest.{Matchers, WordSpec}

class MotionSystemSpecs extends WordSpec with Matchers {

  "MotionSystem" should {
    val world = new GameWorld()
    val motionSystem = new MotionSystem(100, 100)
    "move entity with area, speed and direction" in {
      val snake = SnakeApi.build(Position(20, 20), Right)

      world.add("t", snake)
      world.add("t", Speed(2))
      world.add("t", Right)

      motionSystem.process(world)

      val moved = world.areaComponents("t")

      moved should not be snake
      moved.size shouldBe snake.size
      (moved.head.x - snake.head.x) shouldBe (2 / world.frameRate)
    }
  }

}
