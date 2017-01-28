package models

import exceptions.ModelValidationException

class Session(val id: Int, val sessionKey : String, val userId: Int) {

  override def toString: String = {
    "\"session\": {\"id\":" + {this.id} + ", \"sessionKey\": \"" + {this.sessionKey} + "\", \"userId\": \"" + {this.userId} + "\" }"
  }

}

object Session {

  def apply(id: Int, sessionKey: String, userId: Int): Session = new Session(id, sessionKey, userId)

  def apply(map: Map[String, Any], userId: Int): Session = {
    try {
      val id = map("sessionId").asInstanceOf[Int]
      val sessionKey = map("sessionKey").asInstanceOf[String]
      Session(id = id, sessionKey = sessionKey, userId = userId)
    } catch {
      case e : Exception => {
        throw ModelValidationException(message = "Session creation not valid")
      }
    }
  }
}