package domain.systems

import domain.GameWorld

trait GameSystem {
  def process(world: GameWorld): Unit
}
