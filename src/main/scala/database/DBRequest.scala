package database

import java.sql.CallableStatement

import java.sql.Connection

abstract class DBRequest {
  val sqlQuery : String
  def createStatement(conn : Connection) : CallableStatement
}
