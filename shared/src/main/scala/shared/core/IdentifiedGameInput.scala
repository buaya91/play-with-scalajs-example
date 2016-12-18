package shared.core

import shared.protocol.GameRequest

case class IdentifiedGameInput(playerID: String, cmd: GameRequest)
