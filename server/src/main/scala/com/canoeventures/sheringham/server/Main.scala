package com.canoeventures.sheringham.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import com.canoeventures.sheringham.server.route.RouteBuilder
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App with StrictLogging {

  logger.info("Starting Sheringham application")

  implicit val system = ActorSystem("sheringham")
  implicit val materializer = ActorMaterializer()

  val host = Settings(system).Http.Interface
  val port = Settings(system).Http.Port

  val route = RouteBuilder.build
  val bindingFuture = Http().bindAndHandle(route, host, port)

  bindingFuture onFailure {
    case ex: Exception =>
      logger.error(s"Failed to bind to host $host on port $port: ${ex.getMessage()}")
  }

  sys addShutdownHook {
    logger.info("Shutting down Sheringham application")
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}