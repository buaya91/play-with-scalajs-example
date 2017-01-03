package scala

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.TestKit
import controllers.Application
import org.scalatest._

import scala.concurrent.ExecutionContext
import boopickle.Default._
import shared.protocol._
import shared.serializers.Serializers._

class ApplicationControllerSpec extends TestKit(ActorSystem("Test")) with WordSpecLike with MustMatchers {
  implicit val materializer = ActorMaterializer()(system)
  implicit val ec: ExecutionContext = system.dispatcher

  "websocket endpoint " should {
    val controller = new Application()(system, materializer)
    val flowToTest = controller.wsFlow("SSS")

    val (pub, sub) = TestSource
      .probe[Array[Byte]]
      .via(flowToTest.map(bytes => {
        val bb = ByteBuffer.wrap(bytes)
        Unpickle[GameResponse].fromBytes(bb)
      }))
      .toMat(TestSink.probe[GameResponse])(Keep.both)
      .run()

    val joinGameBB = Pickle.intoBytes[GameRequest](JoinGame("scala"))

    pub.sendNext(bbToArrayBytes(joinGameBB))

    "receive gameState continuously" in {
      sub.request(2)
      sub.expectNext() mustBe an[AssignedID]
      sub.expectNext() must not be an[AssignedID]
    }
  }
}
