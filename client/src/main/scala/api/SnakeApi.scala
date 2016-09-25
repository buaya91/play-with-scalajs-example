package api

import domain._
import domain.components.{Direction, Position, Right}

trait SnakeApi {
  val id: String
  def world: GameWorld
  def addSnake(): Unit
  def changeDir (dir: Direction): Unit
  def speedUp(): Unit
}
