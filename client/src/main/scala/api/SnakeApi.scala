package api

import domain._

trait SnakeApi {
  def createGame(id: String, x: Int, y: Int): GameWorld
  def changeDir (id: String, game: GameWorld, dir: Direction): GameWorld
  def fire(id: String, game: GameWorld): GameWorld
  def step(world: GameWorld):GameWorld
}

object SnakeApiModule extends SnakeApi {
  override def createGame(id: String, x: Int, y: Int): GameWorld =
    GameWorld((x, y), Seq(Snake.build(id, Position(x / 2, y / 2), Right)), Seq())

  override def changeDir(id: String, game: GameWorld, dir: Direction): GameWorld = {
    val changed = game.snakes.map { s =>
      if (s.id == id)
        s.turn(dir)
      else
        s
    }
    game.copy(snakes = changed)
  }

  override def fire(id: String, game: GameWorld): GameWorld = game    // TODO: implement this

  override def step(world: GameWorld): GameWorld = {
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

    world.copy(snacks = eatenSnacks, snakes = eatenSnakes)
  }
}
