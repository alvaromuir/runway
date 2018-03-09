package com.verizon.itanalytics.dataengineering.runway

import akka.actor.{Actor, ActorSystem, Props, Status}
import akka.event.Logging
import com.verizon.itanalytics.dataengineering.runway.messages.SetRequest

import scala.collection.mutable

class Manager extends Actor {
  val map = new mutable.HashMap[String, Object]()
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case SetRequest(key, value) =>
      log.info("received SetRequest - key: {}, value: {}", key, value)
      map.put(key, value)

    case _ =>
      log.error("received unknown message")
      sender() ! Status.Failure(new Exception("unknown message received"))

  }
}

object Main extends App {
  val system = ActorSystem("runway")
  val someActor = system.actorOf(Props[Manager], name = "model")
}
