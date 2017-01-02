import client.infrastructure.ClientPredictor
import monix.reactive.Observable
import org.scalatest._
import shared.protocol.{GameRequest, GameState}
import monix.execution.Scheduler.Implicits.global

import scala.language.postfixOps

class ClientPredictorSpec extends AsyncWordSpec with Matchers {
  implicit override def executionContext = scala.concurrent.ExecutionContext.Implicits.global

  "ClientPredictor" should {
    "able to produce predictions" in {
      val id        = "Test"
      val fakeState = Observable.apply(GameState.init, GameState.init).publish
      val fakeInput = Observable.empty[GameRequest].publish

      val predictions = ClientPredictor.predictions(id, fakeState, fakeInput)

      val f = predictions.take(2).countL.runAsync(global)

      fakeInput.connect()
      fakeState.connect()

      f.map(l => l shouldBe 2)(executionContext)
    }
  }
}
