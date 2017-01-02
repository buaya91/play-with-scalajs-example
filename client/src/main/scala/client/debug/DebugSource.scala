package client.debug

import client.infrastructure.WebSocketSource
import org.scalajs.dom.raw.WebSocket

object DebugSource extends WebSocketSource {
  override lazy val wsConn = new WebSocket("ws://localhost:9000/wsdebug")
}
