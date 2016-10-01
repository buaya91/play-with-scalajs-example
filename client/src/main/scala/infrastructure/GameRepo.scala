package infrastructure

import domain.GameWorld

import scala.concurrent.Future

trait GameRepo {
  def createGame(world: GameWorld): Future[Unit]
}
