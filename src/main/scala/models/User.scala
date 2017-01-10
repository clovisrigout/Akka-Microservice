package models

import exceptions.ModelValidationException

class User(id: Int, fName: String, lName: String, email: String)

object User {

  def apply(id: Int, fName: String, lName: String, email: String): User = new User(id, fName, lName, email)

  def apply(map: Map[String, Any]): User = {
    try {
      val id = map("id").asInstanceOf[Int]
      val fName = map("fName").asInstanceOf[String]
      val lName = map("lName").asInstanceOf[String]
      val email = map("email").asInstanceOf[String]
      User(id = id, fName = fName, lName = lName, email = email)
    } catch {
      case e : Exception => {
        throw ModelValidationException(message = "User creation not valid")
      }
    }
  }

}