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
import models.request.{GetUserRequest, PostNewUserRequest}

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

  private lazy val userRoute =
    pathEnd {
      get {
        complete {
          HttpResponse(StatusCodes.OK, entity = "GET ALL USERS REQUEST")
        }
      } ~
      post {
        parameters("fName".as[String], "lName".as[String], "email".as[String], "password".as[String]) {
          (fName: String, lName: String, email: String, password: String) =>
            val request : PostNewUserRequest = PostNewUserRequest(fName, lName, email, password)
            val responseFuture : Future[Any] = userActor ? request
            onSuccess(responseFuture) {
              case user: User => {
                complete {
                  HttpResponse(StatusCodes.OK, entity = s"$user")
                }
              }
              case e : NoResourceFoundException => {
                complete {
                  HttpResponse(StatusCodes.OK, entity = e.message)
                }
              }
            }
        }
      }
    } ~
    path(IntNumber) { (id) =>
      get {
        val request: GetUserRequest = GetUserRequest(id)
        val responseFuture: Future[Any] = userActor ? request
        onSuccess(responseFuture) {
          case user: User => {
            complete {
              HttpResponse(StatusCodes.OK, entity = s"$user")
            }
          }
          case e: NoResourceFoundException => {
            complete {
              HttpResponse(StatusCodes.OK, entity = e.message)
            }
          }
        }
      }
    }

  val routes =
    path("") {
      helloRoute
    } ~
    pathPrefix("users") {
      userRoute
    }

}

