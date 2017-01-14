package client.infrastructure

import client.domain.ScoreRenderer
import org.scalajs.dom.raw._
import scala.scalajs.js

class DomScoreRenderer(scoreBoard: HTMLTableElement) extends ScoreRenderer {
  override def render(scores: Map[String, Int]) = {
    scores.toIndexedSeq.sortBy(_._2).zipWithIndex.foreach {
      case ((name, score), idx) =>
        val row =
          (if (!js.isUndefined(scoreBoard.rows(idx + 1))) {
            scoreBoard.rows(idx + 1)
          } else {
            scoreBoard.insertRow(-1)
          }).asInstanceOf[HTMLTableRowElement]

        println(row)
        val cell = row.cells

        if (js.isUndefined(cell(0))) {
          row.insertCell(-1)
        }
        if (js.isUndefined(cell(1))) {
          row.insertCell(-1)
        }

        cell(0).textContent = name
        cell(1).textContent = score.toString
    }
  }
}
