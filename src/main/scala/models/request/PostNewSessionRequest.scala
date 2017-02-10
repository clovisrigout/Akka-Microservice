package models.request

import java.sql.CallableStatement
import java.sql.Connection

import database.DBRequest
import models.Session

case class PostNewSessionRequest(email: String, password: String) extends DBRequest {

  val sqlQuery = s"{Call CreateSession(?, ?)}"

  def createStatement(conn : Connection) : CallableStatement = {
    val stmt : CallableStatement = conn.prepareCall(sqlQuery)
    stmt.setString("email", this.email)
    stmt.setString("password", this.password)
    stmt
  }

}
