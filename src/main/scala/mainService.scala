package main.scala

import java.util.concurrent.TimeUnit

import actors.{SessionActor, UserActor}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes, headers}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.pattern.ask
import akka.util.Timeout
import exceptions.NoResourceFoundException

import scala.concurrent.{ExecutionContextExecutor, Future}
import models.{User, Session}
import models.request.{GetUserRequest, PostNewSessionRequest, PostNewUserRequest}

trait MainService {

  implicit val actorSystem: ActorSystem
  implicit val materializer: ActorMaterializer

  implicit def executor: ExecutionContextExecutor

  implicit val timeout = Timeout(5L, TimeUnit.SECONDS)

  lazy val userActor = actorSystem.actorOf(Props[UserActor])
  lazy val sessionActor = actorSystem.actorOf(Props[SessionActor])

  val corsHeaders = List(
    headers.RawHeader("Access-Control-Allow-Origin", "*"),
    headers.RawHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE"),
    headers.RawHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
  )

  val routes = {
    respondWithHeaders(corsHeaders) {
      options {
        complete("")
      } ~
        path("") {
          helloRoute
        } ~
        pathPrefix("users") {
          userRoute
        } ~
        path("sessions") {
          sessionRoute
        }
    }
  }

  private lazy val helloRoute =
    get {
      complete {
        "Hello World"
      }
    }

  private lazy val sessionRoute =
    get {
      complete {
        HttpResponse(StatusCodes.OK, entity = "GET Session request")
      }
    } ~
    post {
      parameters("email".as[String], "password".as[String]) {
        (email: String, password: String) =>
          val request: PostNewSessionRequest = PostNewSessionRequest(email, password)
          val responseFuture: Future[Any] = sessionActor ? request
          onSuccess(responseFuture) {
            case (user : User, session: Session) => {
              complete(s"{$user, $session}")
            }
            case e: NoResourceFoundException => {
              complete {
                HttpResponse(StatusCodes.OK, entity = e.message)
              }
            }
          }
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
            val request: PostNewUserRequest = PostNewUserRequest(fName, lName, email, password)
            val responseFuture: Future[Any] = userActor ? request
            onSuccess(responseFuture) {
              case (user : User, session: Session) => {
                complete(s"{$user, $session}")
              }
              case e: NoResourceFoundException => {
                complete {
                  HttpResponse(StatusCodes.OK, entity = e.message)
                }
              }
              case _ => {
                complete {
                  HttpResponse(StatusCodes.OK, entity = "ALSDASDASDASDA")
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
            complete(s"{$user}")
          }
          case e: NoResourceFoundException => {
            complete {
              HttpResponse(StatusCodes.BadRequest, entity = e.message)
            }
          }
        }
      }
    }

}

