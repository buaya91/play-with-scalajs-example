package domain

import shared.model
import shared.model.{Direction, Down, Position, Up}

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
      case model.Right => oldHead.copy(x = positiveModulo(oldHead.x + 1, boundary._1))
      case model.Left => oldHead.copy(x = positiveModulo(oldHead.x - 1, boundary._1))
    }

    copy(body = newHead +: newTail)
  }

  def turn(newDir: Direction): Snake = {
    (direction, newDir) match {
      case (Up, Down) => this
      case (Down, Up) => this
      case (model.Left, model.Right) => this
      case (model.Right, model.Left) => this
      case _ => copy(direction = newDir)
    }
  }

  def add: Snake = {
    val last = body.last
    val newLast = direction match {
      case Up => last.copy(y = last.y + 1)
      case Down => last.copy(y = last.y - 1)
      case model.Right => last.copy(x = last.x - 1)
      case model.Left => last.copy(x = last.x + 1)
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
  def build(id: String, head: Position, direction: Direction): Snake = {
    def incrementFunc(n: Int): Position = direction match {
      case Up => head.copy(y = head.y + n)
      case Down => head.copy(y = head.y - n)
      case model.Right => head.copy(x = head.x - n)
      case model.Left => head.copy(x = head.x + n)
    }

    val pt = for {
      i <- 1 to 5
    } yield incrementFunc(i)

    Snake(id, head +: pt, direction)
  }
}