package models

import exceptions.ModelValidationException

class User(val id: Int, val username : String)

object User {

  def apply(id: Int, username: String): User = new User(id, username)

  def apply(map: Map[String, Any]): User = {
    try {
      val id = map("id").asInstanceOf[Int]
      val username = map("username").asInstanceOf[String]
      User(id = id, username = username)
    } catch {
      case e : Exception => {
        throw ModelValidationException(message = "User creation not valid")
      }
    }
  }

}