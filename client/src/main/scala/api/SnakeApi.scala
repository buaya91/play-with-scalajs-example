package api

import domain._

trait SnakeApi {
  def changeDir (id: String, dir: Direction): Unit
  def fire(id: String, game: GameWorld): Unit
  def step():Unit
}

class SnakeApiModule(id: String, x: Int, y: Int) extends SnakeApi {
  var world = GameWorld((x, y), Seq(Snake.build(id, Position(x / 2, y / 2), Right)), Seq())

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

    val movedSnakes = world.snakes.map(_.move)

    val eatenSnakes = movedSnakes.map(s => {
      val eaten = addedSnacks.exists(s2 => s.overlapped(s2.position))
      if (eaten)
        s.add
      else
        s
    })

    val eatenSnacks = addedSnacks.filter(s => !movedSnakes.exists(_.overlapped(s.position)))

    world = world.copy(snacks = eatenSnacks, snakes = eatenSnakes)
  }
}
