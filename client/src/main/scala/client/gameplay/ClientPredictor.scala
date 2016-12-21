package client.gameplay

import monix.reactive.Observable
import monix.reactive.observables.ConnectableObservable
import shared.protocol.{GameState, SequencedGameRequest}

import scala.collection.mutable

object ClientPredictor {
  private val predictedState = mutable.Map.empty[GameState]
  private val receivedState = mutable.Seq.empty[GameState]



  def prediction(serverState: ConnectableObservable[GameState],
                 input: ConnectableObservable[SequencedGameRequest]): Observable[GameState] = {
    serverState.connect()

    val firstState = serverState.headL

  }
}
