package test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import game.GameLoop
import org.scalatest._
import shared.model.GameState

import scala.concurrent.Await
import scala.concurrent.duration._

class GameLoopSpec extends WordSpec with Matchers {
  implicit val system = ActorSystem("Test")
  implicit val materializer = ActorMaterializer()

  "Gameloop" should {
    "emit game state periodically" in {
      val src = Source.empty
      val f = src.via(GameLoop.start(1)).runWith(Sink.fold(Seq.empty[GameState])(_ :+ _))
      val result = Await.result(f, 3.seconds)

      result.size shouldBe(4)
    }
  }
}
