package client.infrastructure.views

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object TryAgainPrompt {
  class Backend($ : BackendScope[RetryData, Unit]) {

    def render(data: RetryData) = {
      <.div(^.className := "modal")(
        <.div(^.className := "modal-content")(
          <.div(^.className := "title")("Oops, you're dead"),
          <.div(^.className := "inline")(
            <.button(
              ^.onClick --> Callback(data.onRetry(data.name))
            )("Try Again")
          )
        )
      )
    }
  }

  val component = ReactComponentB[RetryData]("TryAgainPrompt").renderBackend[Backend].build

  def apply(data: RetryData) = component(data)
}

case class RetryData(show: Boolean, onRetry: String => Unit, name: String)
