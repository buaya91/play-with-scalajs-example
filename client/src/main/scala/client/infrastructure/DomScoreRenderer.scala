package client.infrastructure

import client.domain.ScoreRenderer
import org.scalajs.dom._

class DomScoreRenderer(scoreBoard: html.Table) extends ScoreRenderer {
  override def render(scores: Map[String, Int]) = {

  }
}
