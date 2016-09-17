package domain

case class Snake(id: String, body: Seq[Position], direction: Direction) {

  def move: Snake = {
    val newTail = body.dropRight(1)
    val oldHead = body.head
    val newHead = direction match {
      case Up => oldHead.copy(y = oldHead.y + 1)
      case Down => oldHead.copy(y = oldHead.y - 1)
      case Right => oldHead.copy(x = oldHead.x + 1)
      case Up => oldHead.copy(y = oldHead.y + 1)
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
      case Up => last.copy(y = last.y - 1)
      case Down => last.copy(y = last.y + 1)
      case Right => last.copy(x = last.x - 1)
      case Left => last.copy(x = last.x + 1)
    }
    copy(body = body :+ newLast)
  }

  def overlapped(position: Position): Boolean = body.contains(position)
}

object Snake {
  def build(id: String, head: Position, direction: Direction): Snake = {
    def incrementFunc(n: Int): Position = direction match {
      case Up => head.copy(y = head.y + n)
      case Down => head.copy(y = head.y - n)
      case Right => head.copy(x = head.x + n)
      case Left => head.copy(x = head.x - n)
    }

    val pt = for {
      i <- 1 to 5
    } yield incrementFunc(i)

    Snake(id, head +: pt, direction)
  }
}
