package models

import exceptions.ModelValidationException

case class Session(id: Int, sessionKey : String, userId: Int) {

  override def toString: String = {
    "\"session\": {\"id\":" + {this.id} + ", \"sessionKey\": \"" + {this.sessionKey} + "\", \"userId\": \"" + {this.userId} + "\" }"
  }

}

object Session {

  def apply(map: Map[String, Any]): Session = {
    try {
      val id = map("id").asInstanceOf[Int]
      val sessionKey = map("sessionKey").asInstanceOf[String]
      val userId = map("userId").asInstanceOf[Int]
      Session(id = id, sessionKey = sessionKey, userId = userId)
    } catch {
      case e : Exception => {
        throw ModelValidationException(message = "Session creation not valid")
      }
    }
  }

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