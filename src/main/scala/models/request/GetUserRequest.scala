package models.request

import database.DBRequest


class GetUserRequest(id: Int, username: String) {

  final private val sqlQuery = s"SELECT * FROM Users WHERE id = $id"

  final val dBRequest : DBRequest = DBRequest(sqlQuery)

  def getQuery = sqlQuery

}

object GetUserRequest {
  def apply(id: Int, username: String): GetUserRequest = new GetUserRequest(id, username)
}
