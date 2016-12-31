package client.debug

import client.infrastructure.ServerSource
import org.scalajs.dom.raw.WebSocket

object DebugSource extends ServerSource {
  override lazy val wsConn = new WebSocket("ws://localhost:9000/wsdebug")
}
