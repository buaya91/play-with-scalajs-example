package client.refactor

import shared._
import shared.protocol.{GameState, SequencedGameRequest}

import scala.collection.SortedMap

object GlobalData {
  var assignedID: Option[String] = None
  var userName: Option[String]   = None
  var joinedGame: Boolean        = false
}

final class MutableGameData(var serverStateQueue: SortedMap[FrameNo, GameState] = SortedMap.empty,
                            var assignedID: Option[String] = None,
                            var predictedState: SortedMap[FrameNo, GameState] = SortedMap.empty,
                            var unackInput: SortedMap[FrameNo, SequencedGameRequest] = SortedMap.empty)
