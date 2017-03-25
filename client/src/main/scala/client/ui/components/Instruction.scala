package client.ui.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object Instruction {
  private def instruction(show: Boolean) = {
    val hidden = if (show) "" else "hidden"
    <.div(^.id := "tips", ^.className := hidden)("Press space bar to speed up, it will use 1 energy")
  }

  val component = ReactComponentB[Boolean]("Instruction").render_P(instruction).build

  def apply(show: Boolean) = {
    component(show)
  }
}
