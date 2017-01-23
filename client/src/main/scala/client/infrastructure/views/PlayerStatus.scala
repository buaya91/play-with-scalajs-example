package client.infrastructure.views

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

case class PlayerStatus(name: String, score: Int, energy: Int)

object PlayerStatus {
  val ariaHidden = "aria-hidden".reactAttr
  private def divDom(status: PlayerStatus) =
    <.table(
      <.tbody(
        <.tr(
          <.td("Your score"),
          <.td(status.score)
        ),
        <.tr(
          <.td("Energy"),
          <.td(^.className := "energy")(
            for (_ <- 0 to status.energy) yield {
              <.i(^.className := "fa fa-bolt", ariaHidden := "true")
            }
          )
        )
      )
    )
//    <.div(
//      <.div(^.className := "flex-container")(
//        <.div("Your score"),
//        <.div(status.score)
//      ),
//      <.div(^.className := "flex-container")(
//        <.div("Energy"),
//        <.div(
//          for (_ <- 0 to status.energy) yield {
//            <.i(^.className := "fa fa-bolt", ariaHidden := "true")
//          }
//        )
//      )
//    )

  val component = ReactComponentB[PlayerStatus]("StatusBoard").render_P(s => divDom(s)).build

  def apply(status: PlayerStatus) = component(status)
}
