package client.views

import shared.protocol.{DebugNextFrame, GameRequest}

object DebugPanel {
  def apply(send: GameRequest => Unit) = {
    import scalatags.JsDom.all._

    div(
      button("Next frame", onclick := { () =>
        send(GameRequest(DebugNextFrame))
      })
    )
  }
}
