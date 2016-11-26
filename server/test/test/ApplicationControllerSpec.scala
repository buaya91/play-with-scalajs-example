package test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.TestKit
import controllers.Application
import org.scalatest._
import shared.model.GameState

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import prickle._
import serializers._

class ApplicationControllerSpec extends TestKit(ActorSystem("Test")) with WordSpecLike with MustMatchers {
  implicit val materializer = ActorMaterializer()(system)
  implicit val ec: ExecutionContext = system.dispatcher

  "websocket endpoint " should {
    val controller = new Application()(system, materializer)

    "accept ws req" in {
      val flowToTest = controller.wsFlow("SSS")
      val (pub, sub) = TestSource.probe[String].via(flowToTest).toMat(TestSink.probe[String])(Keep.both).run()

      sub.request(1)
      sub.expectNext(2 seconds)
    }

    "receive gameState continuously" in {
      val flowToTest = controller.wsFlow("SSS")
      val (pub, sub) = TestSource.probe[String].via(flowToTest).toMat(TestSink.probe[String])(Keep.both).run()

      sub.request(2)
      sub.expectNext(
        Pickle.intoString[GameState](GameState.init),
        Pickle.intoString[GameState](GameState.init)
      )
    }
  }
}
