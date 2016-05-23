package com.mikemunhall.akkahttpstarter.server.route

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.mikemunhall.akkahttpstarter.server.{Settings, TestActor}

import scala.util.Success

object RouteBuilder {

  def build(system: ActorSystem): Route = {

    implicit val timeout: Timeout = Settings(system).askTimeout

    val testActor = system.actorOf(TestActor.props, "testActor")

    path("api") {
      get {
        (parameter("message")) { message =>
          val future = (testActor ? message)
          onComplete(future) {
            case Success(str: String) => complete(str)
            case Success(e: Exception)   => complete(HttpResponse(StatusCodes.InternalServerError, entity = e.getMessage()))
            case _ => complete(HttpResponse(StatusCodes.InternalServerError, entity = "Unable to complete request"))
          }
        }
      }
    }
  }

}
