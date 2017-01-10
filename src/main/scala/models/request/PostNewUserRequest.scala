package models.request

import java.sql.CallableStatement
import java.sql.Connection

import database.DBRequest

class PostNewUserRequest(val fName: String, val lName: String, val email: String, val password: String) extends DBRequest{

  val sqlQuery = s"{Call CreateUser(?, ?, ?, ?)}"

  def createStatement(conn : Connection) : CallableStatement = {
    val stmt : CallableStatement = conn.prepareCall(sqlQuery)
    stmt.setString("fName", this.fName)
    stmt.setString("lName", this.lName)
    stmt.setString("email", this.email)
    stmt.setString("password", this.password)
    stmt
  }

}

object PostNewUserRequest {
  def apply(fName: String, lName: String, email: String, password: String): PostNewUserRequest =
    new PostNewUserRequest(fName, lName, email, password)
}
