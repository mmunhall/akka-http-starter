package com.mikemunhall.akkahttpstarter.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.mikemunhall.akkahttpstarter.server.route.RouteBuilder
import com.typesafe.scalalogging.StrictLogging
import kamon.Kamon

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App with StrictLogging {

  logger.info("Starting application")

  Kamon.start()

  implicit val system = ActorSystem("akka-http-starter")
  implicit val materializer = ActorMaterializer()

  val host = Settings(system).Http.Interface
  val port = Settings(system).Http.Port

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