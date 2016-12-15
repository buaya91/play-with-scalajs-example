package shared.core

import shared.protocol.{GameCommand, GameRequest}

case class IdentifiedGameInput(id: String, cmd: GameRequest)
