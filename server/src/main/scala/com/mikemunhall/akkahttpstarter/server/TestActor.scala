package com.mikemunhall.akkahttpstarter.server

import akka.actor.{Actor, Props, Stash}
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import kamon.akka.ActorMetrics

object TestActor {
  def props = Props[TestActor]
  val counter = Kamon.metrics.counter("test-actor-counter")
}

class TestActor extends Actor with Stash with StrictLogging {
  self ! "initialize"

  def receive = uninitialized

  def uninitialized: Receive = {
    case "initialize" =>
      logger.debug("Initializing TestActor and unstashing messages.")
      unstashAll()
      context.become(initialized)
    case _ =>
      logger.debug("TestActor invoked before initialization. Stashing message.")
      stash()
  }

  def initialized: Receive = {
    case "fail" =>
      TestActor.counter.increment()
      logger.debug(s"""TestActor received message "fail"""")
      sender() ! new Exception("Fail conditions met")
    case str: String =>
      TestActor.counter.increment()
      logger.debug(s"""TestActor received message "$str"""")
      sender() ! s"$str back at ya."
  }
}