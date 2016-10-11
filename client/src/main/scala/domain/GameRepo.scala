package domain

import domain.components._
import monix.reactive.Observable
import scala.concurrent.Future

trait GameRepo {
  def init(): Future[GameWorld]

  def subscribeToAllEvents(): Observable[GlobalEvent]

  def broadcastEvent(ev: GlobalEvent): Unit
}
