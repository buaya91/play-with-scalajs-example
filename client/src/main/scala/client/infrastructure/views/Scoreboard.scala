package client.infrastructure.views

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object Scoreboard {
  private def tableDom(scores: Map[String, Int]) = <.table(
    <.thead(
      <.tr(
        <.th("Name"),
        <.th("Score")
      )
    ),
    <.tbody(
      scores.map {
        case (n, scr) =>
          <.tr(
            <.td(n),
            <.td(scr)
          )
      }
    )
  )

  val component = ReactComponentB[Map[String, Int]]("Scoreboard").render_P(tableDom).build

  def apply(scores: Map[String, Int]) = {
    component(scores)
  }
}
