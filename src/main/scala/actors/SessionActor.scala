package actors

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import database.DBResponse
import exceptions.NoResourceFoundException
import models.{Session, User}
import models.request.{GetSessionRequest, PostNewSessionRequest}

import scala.concurrent.Future

class SessionActor extends Actor with ActorLogging {

  override def receive : Actor.Receive = {

    case request : GetSessionRequest => {
      log.info("Received GetSessionRequest")
      log.info(s"$request")
      val db : ActorRef = context.actorOf(Props(classOf[DBActor]))
      implicit val timeout = Timeout(5L, TimeUnit.SECONDS)
      implicit val executionContext = context.system.dispatcher
      val f : Future[Any] = db ? request
      val parent = sender
      f.onSuccess({
        case dbResponse : DBResponse => {
          log.info("Received DBResponse")
          if(dbResponse.resultMap.nonEmpty){
            log.info(s"Received non-empty DBResponse")
            val session : Session = Session(dbResponse.resultMap.head)
            log.info(s"$session")
            parent ! (session)
          } else {
            log.info("Received empty DBResponse")
            parent ! NoResourceFoundException(message = s"No user found for query ${request.sqlQuery}")
          }
        }
      })
    }
    case request : PostNewSessionRequest => {
      log.info("Received PostNewSessionRequest")
      log.info(s"$request")
      val db : ActorRef = context.actorOf(Props(classOf[DBActor]))
      implicit val timeout = Timeout(5L, TimeUnit.SECONDS)
      implicit val executionContext = context.system.dispatcher
      val f : Future[Any] = db ? request
      val parent = sender
      f.onSuccess({
        case dbResponse : DBResponse => {
          log.info("Received DBResponse")
          if(dbResponse.resultMap.nonEmpty){
            log.info(s"Received non-empty DBResponse")
            val user : User = User(dbResponse.resultMap.head)
            log.info(s"$user")
            val session : Session = Session(dbResponse.resultMap.head, user.id)
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