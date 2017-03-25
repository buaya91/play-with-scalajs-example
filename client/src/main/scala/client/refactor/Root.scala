package client.refactor

import client.ui.components._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import shared.protocol.GameState

object Root {

  def rootDom(rootData: RootData) = {
    val status = rootData.state.snakes.collectFirst {
      case snk if snk.id == rootData.id => PlayerStatus(snk.name, snk.body.length, snk.energy)
    }

    val scores = rootData.state.snakes.map(s => s.name -> s.body.size).toMap

    <.div(
      Instruction(rootData.showInstruction),
      rootData.retry.show ?= TryAgainPrompt(rootData.retry),
      <.div(^.id := "page")(
        <.div(^.id := "side-panel")(
          Scoreboard(scores),
          status.map(PlayerStatus(_))
        ),
        GameCanvas(rootData.state, rootData.id)
      )
    )
  }

  val component = ReactComponentB[RootData]("Root").render_P(s => rootDom(s)).build

  def apply(data: RootData) = component(data)
}

case class RootData(id: String,
                    state: GameState,
                    showInstruction: Boolean,
                    retry: RetryData)
