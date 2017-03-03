package client.infrastructure.views

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object TryAgainPrompt {
  class Backend($ : BackendScope[() => Unit, Unit]) {

    def render(onSubmit: () => Unit) = {
      <.div(^.className := "modal")(
        <.div(^.className := "modal-content")(
          <.div(^.className := "title")("Oops, you're dead"),
          <.div(^.className := "inline")(
            <.button(
              ^.onClick --> Callback(onSubmit)
            )("Try Again")
          )
        )
      )
    }
  }

  val component = ReactComponentB[() => Unit]("TryAgainPrompt").renderBackend[Backend].build

  def apply(onSubmit: () => Unit) = component(onSubmit)
}
