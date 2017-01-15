package client.infrastructure

import client.domain.ScoreRenderer
import org.scalajs.dom.raw._

class DomScoreRenderer(scoreBoard: HTMLTableElement) extends ScoreRenderer {
  override def render(scores: Map[String, Int]) = {
    val rowsStr = (1 to 10).map(_ => "<tr><td></td><td></td></tr>").mkString
    scoreBoard.innerHTML = s"""
        |<thead>
        | <tr>
        |   <th>Name</th>
        |   <th>Score</th>
        | </tr>
        |</thead>
        |<tbody>
        |$rowsStr
        |</tbody>""".stripMargin

    scores.take(10).toIndexedSeq.sortBy(_._2).zipWithIndex.foreach {
      case ((name, score), idx) =>
        val row = scoreBoard.rows(idx + 1).asInstanceOf[HTMLTableRowElement]

        val cell = row.cells

        cell(0).textContent = name
        cell(1).textContent = score.toString
    }
  }
}
