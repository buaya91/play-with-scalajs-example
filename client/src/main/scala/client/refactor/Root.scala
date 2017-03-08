package client.refactor

import client.infrastructure.views._
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
      <.div(^.id := "page")(
        GameCanvas(rootData.state, rootData.id),
        <.div(^.id := "side-panel")(
          Scoreboard(scores),
          status.map(PlayerStatus(_))
        )
      )
    )
  }

  val component = ReactComponentB[RootData]("Root").render_P(s => rootDom(s)).build

  def apply(data: RootData) = component(data)
}

case class RootData(showInstruction: Boolean, id: String, state: GameState)
