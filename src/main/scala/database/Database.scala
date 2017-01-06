package database

import java.sql._

import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ListBuffer

object Database {

    val config = ConfigFactory.load()

    val name = config.getString("mysql.name")
    val host = config.getString("mysql.host")
    val port = config.getInt("mysql.port")

    val username = config.getString("mysql.username")
    val password = config.getString("mysql.password")
    val driver = config.getString("mysql.driver")

    val url = "jdbc:mysql://%s:%d/%s".format(host, port, name)

    private var connection : Connection = null

    def executeQuery(query : String) : DBResponse = {
      try {
        if (!this.isConnected()) {
          this.dbConnect()
        }
        val statement: Statement = this.connection.createStatement
        val rs: ResultSet = statement.executeQuery(query)
        val resultMap : List[Map[String, Any]] = makeMapFromRS(rs)
        this.dbClose()
        DBResponse(resultMap)
      } catch {
        case e : Exception => {
          e.printStackTrace()
          throw e
        }
      }
    }

    def dbConnect() = {
        try {
            // Class.forName(this.driver)
            connection = DriverManager.getConnection(this.url, this.username, this.password)
        } catch {
            case e: Exception => e.printStackTrace()
        }
    }

    def dbClose() = {
        try {
            this.connection.close()
        } catch {
            case e: Exception => e.printStackTrace()
        }
    }

    def isConnected() : Boolean = {
        if (this.connection != null) {
            !this.connection.isClosed
        } else {
            false
        }
    }

    def makeMapFromRS(rs : ResultSet) : List[Map[String, Any]] = {
      val rows = ListBuffer[Map[String, Any]]()
      val metaData : ResultSetMetaData = rs.getMetaData
      val colCount : Int = metaData.getColumnCount

      while(rs.next()){
        val columns: Map[String, Any] = Range(1, colCount+1)
          .map(i => metaData.getColumnLabel(i) -> rs.getObject(i))
          .toMap
        rows += columns
      }
      rows.toList
    }

}