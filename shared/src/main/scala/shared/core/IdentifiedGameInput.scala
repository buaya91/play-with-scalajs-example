package shared.core

import shared.protocol.GameCommand

case class IdentifiedGameInput(id: String, cmd: GameCommand)
