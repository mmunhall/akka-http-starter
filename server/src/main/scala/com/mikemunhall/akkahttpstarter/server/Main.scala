package com.mikemunhall.akkahttpstarter.server

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.mikemunhall.akkahttpstarter.server.route.RouteBuilder
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon
import kamon.akka.ActorMetrics

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App with StrictLogging {

  logger.info("Starting application")

  // create actory system
  implicit val system = ActorSystem("akka-http-starter")
  implicit val materializer = ActorMaterializer()

  // initialize Kamon
  Kamon.start()
  val actorMetrics = Kamon.metrics.entity(ActorMetrics, "test-actor-metrics")
  val metricsSubscriber = system.actorOf(Props[MetricsSubscriber], "metrics-subscriber")
  Kamon.metrics.subscribe("counter", "**", metricsSubscriber)

  // fetch externalized configuration settings
  val host = Settings(system).Http.Interface
  val port = Settings(system).Http.Port

  // create route and HTTP server
  val route = RouteBuilder.build(system)
  val bindingFuture = Http().bindAndHandle(route, host, port)

  bindingFuture onFailure {
    case ex: Exception =>
      logger.error(s"Failed to bind to host $host on port $port: ${ex.getMessage()}")
  }

  sys addShutdownHook {
    logger.info("Shutting down application")
    Kamon.shutdown()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}