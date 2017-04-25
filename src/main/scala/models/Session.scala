package models

import exceptions.ModelValidationException

case class Session(sessionKey : String, userId: Int) {

  override def toString: String = {
    "\"session\": {\"sessionKey\": \"" + {this.sessionKey} + "\", \"userId\": \"" + {this.userId} + "\" }"
  }

}

object Session {

  def apply(map: Map[String, Any]): Session = {
    try {
      val sessionKey = map("session_key").asInstanceOf[String]
      val userId = map("id").asInstanceOf[Int]
      Session(sessionKey = sessionKey, userId = userId)
    } catch {
      case e : Exception => {
        throw ModelValidationException(message = "Session creation not valid")
      }
    }
  }

}