package client.views

import shared.protocol.{DebugNextFrame, GameRequest}

object DebugPanel {
  import scalatags.JsDom.all._

  def apply(send: GameRequest => Unit) = {
    div(
      button("Next frame", onclick := { () =>
        send(GameRequest(DebugNextFrame))
      })
    )
  }
}
