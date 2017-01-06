package actors

import akka.actor.{Actor, ActorLogging}
import database.{DBRequest, DBResponse, Database}

class DBActor extends Actor with ActorLogging {

  override def receive : Actor.Receive = {
    case request : DBRequest => {
      log.info("Recieved DBRequest")
      val response : DBResponse = Database.executeQuery(request.query)
      log.info(s"Executed query")
      sender ! response
    }
  }
}
