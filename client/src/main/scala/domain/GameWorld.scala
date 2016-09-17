package domain

import scala.util.Random

case class GameWorld(size: (Int, Int), snakes: Seq[Snake], snacks: Seq[Snacks]) {

  def throwSnacks: Seq[Snacks] = {
    val toAdd = Math.max(1, snakes.size - 1)
    val snk = for {
      i <- 0 to toAdd
    } yield {
      val rX = Random.nextInt() % size._1
      val rY = Random.nextInt() % size._2
      Chocolate(Position(rX, rY))
    }

    snacks ++ snk
  }
}
