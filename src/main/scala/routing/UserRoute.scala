package routing

import java.util.concurrent.TimeUnit

import actors.UserActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.stream.ActorMaterializer
import akka.util.Timeout
import exceptions.NoResourceFoundException
import models.User
import models.request.{GetUserRequest, PostNewUserRequest}

import scala.concurrent.{ExecutionContextExecutor, Future}

case class UserRoute() extends Route {

  private lazy val userRoute : server.Route =
    pathEnd {
      get {
        complete {
          HttpResponse(StatusCodes.OK, entity = "GET ALL USERS REQUEST")
        }
      }
    }

  def getRoute() = {
    this.userRoute
  }
}
