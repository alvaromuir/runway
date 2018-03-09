package com.verizon.itanalytics.dataengineering.runway

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import com.verizon.itanalytics.dataengineering.runway.messages.SetRequest
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


class ManagerSpec extends TestKit(ActorSystem("ManagerSpec",
  ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
  with WordSpecLike with ImplicitSender with Matchers with BeforeAndAfterAll {


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val actorRef = TestActorRef(new Manager)
  val manager = actorRef.underlyingActor
  val errorMsg = "received unknown message"

  "The Manager" should  {
    "set and get valid messages" in {
      actorRef ! SetRequest("key", "value")
      manager.map.get("key") should equal(Some("value"))
    }

    "log un-recognized message types" in {
      EventFilter.error(message = errorMsg, occurrences = 1) intercept {
        actorRef ! "bad message"
      }
    }
  }
}
