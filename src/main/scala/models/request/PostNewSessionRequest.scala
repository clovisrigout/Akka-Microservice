package models.request

import java.sql.CallableStatement
import java.sql.Connection

import database.DBRequest

class PostNewSessionRequest(val email: String, val password: String) extends DBRequest {

  val sqlQuery = s"{Call CreateSession(?, ?)}"

  def createStatement(conn : Connection) : CallableStatement = {
    val stmt : CallableStatement = conn.prepareCall(sqlQuery)
    stmt.setString("email", this.email)
    stmt.setString("password", this.password)
    stmt
  }

}

object PostNewSessionRequest {
  def apply(email: String, password: String): PostNewSessionRequest =
    new PostNewSessionRequest(email, password)
}
