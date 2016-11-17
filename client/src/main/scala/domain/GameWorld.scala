package domain

import shared.model.Position

import scala.util.Random

case class GameWorld(size: (Int, Int), snakes: Seq[Snake], snacks: Seq[Snacks]) {

  def throwSnacks: Seq[Snacks] = {
    val toAdd = (snakes.size * 3) - snacks.size

    val snk = for {
      i <- 0 to toAdd
    } yield {
      val rX = Math.abs(Random.nextInt()) % size._1
      val rY = Math.abs(Random.nextInt()) % size._2
      Chocolate(Position(rX, rY))
    }

    snacks ++ snk
  }
}
