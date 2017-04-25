package main.scala

import java.util.concurrent.TimeUnit

import actors.{SessionActor, UserActor}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes, headers}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import exceptions.NoResourceFoundException

import scala.concurrent.{ExecutionContextExecutor, Future}
import models.{Session, User}
import models.request.{GetSessionRequest, GetUserRequest, PostNewSessionRequest, PostNewUserRequest}

trait MainService {

  implicit val actorSystem: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit def executor: ExecutionContextExecutor
  implicit val timeout = Timeout(5L, TimeUnit.SECONDS)

  lazy val userActor = actorSystem.actorOf(Props[UserActor])
  lazy val sessionActor = actorSystem.actorOf(Props[SessionActor])

  implicit val newUserFormat = jsonFormat(PostNewUserRequest, "fName", "lName", "email", "password")
  implicit val getUserFormat = jsonFormat(GetUserRequest, "id", "sessionKey")
  implicit val newSessionFormat = jsonFormat(PostNewSessionRequest, "email", "password")
  implicit val getSessionFormat = jsonFormat(GetSessionRequest, "sessionKey", "userId")
  implicit val userFormat = jsonFormat(User.apply, "id", "fName", "lName", "email")
  implicit val sessionFormat = jsonFormat(Session.apply, "userId", "sessionKey")

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


  private lazy val helloRoute: Route = { ctx =>
    ctx.complete {
      "Hello World"
    }
  }

  private lazy val sessionRoute : Route =
    get {
      entity(as[GetSessionRequest]) { request =>
        val responseFuture: Future[Any] = sessionActor ? request
        onSuccess(responseFuture) {
          case (session: Session) => {
            complete(s"{$session}")
          }
          case e: NoResourceFoundException => {
            complete {
              HttpResponse(StatusCodes.NoContent, entity = e.message)
            }
          }
        }
      }
    } ~
    post {
      entity(as[PostNewSessionRequest]) { request =>
        val responseFuture: Future[Any] = sessionActor ? request
        onSuccess(responseFuture) {
          case (user : User, session: Session) => {
            complete(s"{$user, $session}")
          }
          case e: NoResourceFoundException => {
            complete {
              HttpResponse(StatusCodes.NoContent, entity = e.message)
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
        entity(as[PostNewUserRequest]) { request =>
          val responseFuture: Future[Any] = userActor ? request
          onSuccess(responseFuture) {
            case (user : User, session: Session) => {
              complete(s"{$user, $session}")
            }
            case e: NoResourceFoundException => {
              complete {
                HttpResponse(StatusCodes.NoContent, entity = e.message)
              }
            }
            case _ => {
              complete {
                HttpResponse(StatusCodes.BadRequest, entity = "")
              }
            }
          }
        }
      }
    } ~
    path(IntNumber) { (id) =>
      get {
        entity(as[GetUserRequest]) { request =>
          val responseFuture: Future[Any] = userActor ? request
          onSuccess(responseFuture) {
            case user: User => {
              complete(s"{$user}")
            }
            case e: NoResourceFoundException => {
              complete {
                HttpResponse(StatusCodes.NoContent, entity = e.message)
              }
            }
          }
        }
      }
    }

}

