package models.request

import java.sql.{CallableStatement, Connection}

import database.DBRequest


case class GetSessionRequest(sessionToken: String, userId: Int) extends DBRequest{

  val sqlQuery = s"{Call GetSession(?, ?)}"

  def createStatement(conn : Connection) : CallableStatement = {
    val stmt : CallableStatement = conn.prepareCall(sqlQuery)
    stmt.setString("sessionKey", this.sessionToken)
    stmt.setInt("userId", this.userId)
    stmt
  }

}
