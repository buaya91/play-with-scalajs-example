package client.domain

import monix.reactive.Observable
import shared.protocol.{GameRequest, GameResponse}

trait AuthorityState {

  def stream: Observable[GameResponse]

  def request(input: GameRequest): Unit
}
