package actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import database.DBResponse
import exceptions.NoResourceFoundException
import models.{Session, User}
import models.request.{GetUserRequest, PostNewUserRequest}

import scala.concurrent.Future

class UserActor extends Actor with ActorLogging {

  override def receive : Actor.Receive = {
    case request : GetUserRequest => {
      log.info("Received GetUserRequest")
      val db : ActorRef = context.actorOf(Props(classOf[DBActor]))
      implicit val timeout = Timeout(5L, TimeUnit.SECONDS)
      implicit val executionContext = context.system.dispatcher
      val f : Future[Any] = db ? request
      val parent = sender
      f.onSuccess({
        case dbResponse : DBResponse => {
          log.info("Received DBResponse")
          if(dbResponse.resultMap.nonEmpty){
            log.info("Received non-empty DBResponse")
            val user : User = User(dbResponse.resultMap.head)
            parent ! user
          } else {
            log.info("Received empty DBResponse")
            parent ! NoResourceFoundException(message = s"No user found for query ${request.sqlQuery}")
          }
        }
      })
    }
    case request : PostNewUserRequest => {
      log.info("Received PostNewUserRequest")
      val db : ActorRef = context.actorOf(Props(classOf[DBActor]))
      implicit val timeout = Timeout(5L, TimeUnit.SECONDS)
      implicit val executionContext = context.system.dispatcher
      val f : Future[Any] = db ? request
      val parent = sender
      f.onSuccess({
        case dbResponse : DBResponse => {
          log.info("Received DBResponse")
          if(dbResponse.resultMap.nonEmpty){
            log.info(s"Received non-empty DBResponse with id = ${dbResponse.resultMap.head("id")}")
            val user : User = User(dbResponse.resultMap.head)
            log.info(s"$user")
            val session = Session(dbResponse.resultMap.head, user.id)
            log.info(s"$session")
            parent ! (user, session)
          } else {
            log.info("Received empty DBResponse")
            parent ! NoResourceFoundException(message = s"No user found for query ${request.sqlQuery}")
          }
        }
      })
    }

  }
}