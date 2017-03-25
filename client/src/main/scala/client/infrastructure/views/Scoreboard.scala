package client.infrastructure.views

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.ReactAttr
import japgolly.scalajs.react.vdom.prefix_<^._

object Scoreboard {
  val dataIcon           = ReactAttr.Generic("data-icon")
  val dataStyle          = ReactAttr.Generic("data-style")
  val dataCountHref      = ReactAttr.Generic("data-count-href")
  val dataCountApi       = ReactAttr.Generic("data-count-api")
  val dataCountAriaLabel = ReactAttr.Generic("data-count-aria-label")
  val ariaLabel          = ReactAttr.Generic("aria-label")

  private def tableDom(scores: Map[String, Int]) =
    <.div(
      <.p(^.className := "title")("Top 10 !!"),
      <.table(
        <.thead(
          <.tr(
            <.th("Name"),
            <.th("Score")
          )
        ),
        <.tbody(
          scores.map {
            case (n, scr) =>
              <.tr(^.key := n)(
                <.td(n),
                <.td(scr)
              )
          }
        )
      )
    )

  val component = ReactComponentB[Map[String, Int]]("Scoreboard").render_P(tableDom).build

  def apply(scores: Map[String, Int]) = {
    component(scores)
  }
}
