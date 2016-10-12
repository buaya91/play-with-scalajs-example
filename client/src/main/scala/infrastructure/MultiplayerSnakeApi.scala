package infrastructure

import api.SnakeApi
import domain.components.GlobalEvent
import monix.reactive.Observable

trait MultiplayerSnakeApi extends SnakeApi {
  def onServerEvents(eventStream: Observable[GlobalEvent]): Unit
}
