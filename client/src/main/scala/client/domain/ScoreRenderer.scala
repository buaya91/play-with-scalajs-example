package client.domain

trait ScoreRenderer {
  def render(scores: Map[String, Int]): Unit
}
