package client.infrastructure.views

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

case class PlayerStatus(name: String, score: Int, energy: Int)

object PlayerStatus {
  val ariaHidden = "aria-hidden".reactAttr
  private def divDom(status: PlayerStatus) =
    <.div(
      <.div(
        <.div(status.name),
        <.div(status.score)
      ),
      <.div(
        for (_ <- 0 to status.energy) yield {
          <.i(^.className := "fa fa-bolt", ariaHidden := "true")
        }
      )
    )

  val component = ReactComponentB[PlayerStatus]("StatusBoard")
    .render_P(s => divDom(s))
    .build

  def apply(status: PlayerStatus) = component(status)
}
