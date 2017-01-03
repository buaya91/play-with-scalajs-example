package scala

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

class PlayerActorSpec extends TestKit(ActorSystem("Test")) with WordSpecLike with MustMatchers {

}
