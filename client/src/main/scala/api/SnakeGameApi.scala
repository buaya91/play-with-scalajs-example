package api

import domain._

trait SnakeGameApi {
  def addSnake(snakeName: String): Unit
  def changeDir (id: String, dir: Direction): Unit
  def fire(id: String, game: GameWorld): Unit
  def step():Unit
}

class SnakeGameApiModule(id: String, x: Int, y: Int) extends SnakeGameApi {
  var ended = false

  var world = GameWorld((x, y), Seq(), Seq())

  override def changeDir(id: String, dir: Direction): Unit = {
    val changed = world.snakes.map { s =>
      if (s.id == id)
        s.turn(dir)
      else
        s
    }
    world = world.copy(snakes = changed)
  }

  override def fire(id: String, game: GameWorld): Unit = game    // TODO: implement this

  override def step(): Unit = {
    val addedSnacks = world.throwSnacks

    val movedSnakes = world.snakes
      .map(_.move(world.size))
      .filterNot(_.bumpedToSelf)

    val snakeAlive = movedSnakes.filterNot(s1 => {
      val bumpToOtherSnakes: Boolean = (for {
        s2 <- movedSnakes
        if s1 != s2 && s1.bumpToOthers(s2.body)
      } yield true).nonEmpty

      bumpToOtherSnakes
    })

    val eatenSnakes = snakeAlive.map(s => {
      val eaten = addedSnacks.exists(s2 => s.overlapped(s2.position))
      if (eaten)
        s.add
      else
        s
    })

    val eatenSnacks = addedSnacks.filter(s => !movedSnakes.exists(_.overlapped(s.position)))

    if (eatenSnakes.isEmpty)
      ended = true

    world = world.copy(snacks = eatenSnacks, snakes = eatenSnakes)
  }

  override def addSnake(snakeName: String): Unit = {
    ???
  }
}
