package models.request

import java.sql.{CallableStatement, Connection}

import database.DBRequest


case class GetUserRequest(id: Int) extends DBRequest{

  val sqlQuery = s"{Call GetUser(?)}"

  def createStatement(conn : Connection) : CallableStatement = {
    val stmt : CallableStatement = conn.prepareCall(sqlQuery)
    stmt.setInt("id", this.id)
    stmt
  }

}
