package models.request

import java.sql.CallableStatement
import java.sql.Connection

import database.DBRequest

case class PostNewUserRequest(fName: String, lName: String, email: String, password: String) extends DBRequest{

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
