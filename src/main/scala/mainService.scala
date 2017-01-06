package main.scala

import java.util.concurrent.TimeUnit

import actors.UserActor
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.pattern.ask
import akka.util.Timeout
import exceptions.NoResourceFoundException

import scala.concurrent.{ExecutionContextExecutor, Future}
import models.User
import models.request.GetUserRequest

trait MainService {

  implicit val actorSystem: ActorSystem
	implicit val materializer: ActorMaterializer
	implicit def executor: ExecutionContextExecutor
  implicit val timeout = Timeout(5L, TimeUnit.SECONDS)

  lazy val userActor = actorSystem.actorOf(Props[UserActor])

  private lazy val helloRoute =
    get {
      complete {
        "Hello World"
      }
    }

  private def complexRoute(id : Int) : Route =
    parameters("username", "email".as[String]?) { (username : String, email : Option[String]) =>
      val request : GetUserRequest = GetUserRequest(id, username)
      val responseFuture : Future[Any] = userActor ? request
      onSuccess(responseFuture) {
        case user: User => {
          complete {
            HttpResponse(StatusCodes.OK, entity = user.username)
          }
        }
        case e : NoResourceFoundException => {
          complete {
            HttpResponse(StatusCodes.OK, entity = e.message)
          }
        }
      }
    }

  val routes =
    path("hello") {
      helloRoute
    } ~
    path("users" / IntNumber ) { (id) =>
      complexRoute(id)
    }

}

