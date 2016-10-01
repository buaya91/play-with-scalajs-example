package api

import domain._
import domain.components.{Direction, Position, Right}

trait SnakeApi {
  def world: GameWorld

  // to add new snake
  def addNewSnake(id: String): Unit
  def changeDir(id: String, dir: Direction): Unit
  def speedUp(id: String): Unit
}
