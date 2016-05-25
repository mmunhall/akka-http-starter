package com.mikemunhall.akkahttpstarter.server

import akka.actor.Actor
import com.typesafe.scalalogging.StrictLogging
import kamon.metric.SubscriptionsDispatcher.TickMetricSnapshot

object MetricsSubscriber {

}

class MetricsSubscriber extends Actor with StrictLogging {
  def receive = {
    case tickSnapshot: TickMetricSnapshot =>
      val counters = tickSnapshot.metrics.filterKeys(_.category == "counter")
      counters.foreach { case (e, s) =>
        val counterSnapshot = s.counter("counter").get
        logger.debug("Counter [%s] was incremented [%d] times.".format(e.name, counterSnapshot.count))
      }
    case _ =>
      logger.debug("unknown metrics event received")
  }
}
