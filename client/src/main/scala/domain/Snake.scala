package domain

case class Snake(id: String, body: Seq[Position], direction: Direction) {

  private def positiveModulo(a: Int, n: Int): Int = {
    // to get positive mod
    // we compute remainder, and add back the n
    // to prevent result bigger than n which violate the rules
    // we take the remainder again
    ((a % n) + n) % n
  }

  def move(boundary: (Int, Int)): Snake = {
    val newTail = body.dropRight(1)
    val oldHead = body.head
    val newHead = direction match {
      case Up => oldHead.copy(y = positiveModulo(oldHead.y - 1, boundary._2))
      case Down => oldHead.copy(y = positiveModulo(oldHead.y + 1, boundary._2))
      case Right => oldHead.copy(x = positiveModulo(oldHead.x + 1, boundary._1))
      case Left => oldHead.copy(x = positiveModulo(oldHead.x - 1, boundary._1))
    }

    copy(body = newHead +: newTail)
  }

  def turn(newDir: Direction): Snake = {
    (direction, newDir) match {
      case (Up, Down) => this
      case (Down, Up) => this
      case (Left, Right) => this
      case (Right, Left) => this
      case _ => copy(direction = newDir)
    }
  }

  def add: Snake = {
    val last = body.last
    val newLast = direction match {
      case Up => last.copy(y = last.y + 1)
      case Down => last.copy(y = last.y - 1)
      case Right => last.copy(x = last.x - 1)
      case Left => last.copy(x = last.x + 1)
    }
    copy(body = body :+ newLast)
  }

  def overlapped(position: Position): Boolean = body.contains(position)

  def bumpToOthers(othersBody: Seq[Position]): Boolean = {
    othersBody.contains(body.head)
  }

  def bumpedToSelf: Boolean = body.distinct.size != body.size
}

object Snake {

}
