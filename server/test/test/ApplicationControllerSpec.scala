package test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.TestKit
import controllers.Application
import org.scalatest._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ApplicationControllerSpec extends TestKit(ActorSystem("Test")) with WordSpecLike with MustMatchers {
//  "websocket endpoint " should {
//    "accept ws req" in {
//      implicit val materializer = ActorMaterializer()(system)
//      implicit val ec: ExecutionContext = system.dispatcher
//
//      val controller = new Application()(system, materializer)
//      val flowToTest = controller.wsFlow("SSS")
//
//      val (pub, sub) = TestSource.probe[String].via(flowToTest).toMat(TestSink.probe[String])(Keep.both).run()
//
//      sub.request(1)
//      sub.expectNext(5 seconds)
//    }
//  }
}
