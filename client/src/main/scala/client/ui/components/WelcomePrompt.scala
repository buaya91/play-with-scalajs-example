package client.ui.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^.{<, _}

object WelcomePrompt {
  class Backend($ : BackendScope[String => Unit, String]) {

    def inputText(eventI: ReactEventI): Callback = {
      $.setState(eventI.target.value)
    }

    def onEnterKeyed(eventI: ReactKeyboardEventI): CallbackTo[Boolean] = CallbackTo {
      eventI.keyCode == 13
    }

    def render(onSubmit: String => Unit) = {
      <.div(^.className := "modal")(
        <.div(^.className := "modal-content")(
          <.div(^.className := "title")("Welcome to Orochi"),
          <.div("Enter your nick name"),
          <.div(
            ^.className := "inline",
            <.input(
              ^.autoFocus := true,
              ^.defaultValue := "",
              ^.onChange ==> inputText,
              ^.onKeyUp ==> { (ev: ReactKeyboardEventI) =>
                onEnterKeyed(ev).flatMap(t => if (t) $.state.map(onSubmit) else Callback.empty)
              }
            ),
            <.button(
              ^.onClick --> $.state.map(onSubmit)
            )("Submit")
          )
        )
      )
    }
  }

  val component = ReactComponentB[String => Unit]("WelcomePrompt").initialState("").renderBackend[Backend].build

  def apply(onSubmit: String => Unit) = component(onSubmit)
}
