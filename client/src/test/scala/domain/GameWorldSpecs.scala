package domain

import domain.components.Speed
import domain.systems.{CollisionSystem, MotionSystem}
import org.scalatest.{Matchers, WordSpec}

class GameWorldSpecs extends WordSpec with Matchers {
  "GameWorld" can {
    val world = new GameWorld()

    "add components" in {
      world.add("test", true)
      world.isSnakeComponents.size shouldBe 1
    }

    "add system" in {
      world.addSystem(new MotionSystem(100, 100))
      world.systems.size shouldBe 1
    }

    "remove component" in {
      world.add("test", Speed(10))

      world.speedComponents.size shouldBe 1

      world.remove("test")

      world.speedComponents.size shouldBe 0
    }
  }

  it should {
    val world = new GameWorld()
    "store system in same order of inserting" in {
      val motion = new MotionSystem(100, 100)
      val collision = new CollisionSystem()

      world.addSystem(motion)
      world.addSystem(collision)

      world.systems(0) shouldBe motion
      world.systems(1) shouldBe collision
    }
  }
}
