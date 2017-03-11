package client.refactor

import shared._
import shared.protocol.{GameState, SequencedGameRequest}

import scala.collection.SortedMap

object GlobalData {
  var serverStateQueue: SortedMap[FrameNo, GameState] = SortedMap.empty
  var assignedID: Option[String]                      = None
  var userName: Option[String]                        = None
  var predictedState                                  = SortedMap.empty[FrameNo, GameState]
  var unackInput                                      = SortedMap.empty[FrameNo, SequencedGameRequest]

  var joinedGame: Boolean = false
  var showRetry: Boolean  = false
  var metaStatus          = PlayerMetaStatus(joinedGame = false, dead = false)
}

case class PlayerMetaStatus(joinedGame: Boolean, dead: Boolean) {
  def die(): PlayerMetaStatus = copy(joinedGame = false, dead = true)
}
