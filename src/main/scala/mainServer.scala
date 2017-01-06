package main.scala

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
 
object MainServer extends App with MainService {
 
	override implicit val actorSystem = ActorSystem("server")
	override implicit val materializer = ActorMaterializer()
	override implicit val executor = actorSystem.dispatcher

	val config = ConfigFactory.load()
	val host = config.getString("http.host")
	val port = config.getInt("http.port")

	startServer(host, port)

	def startServer(host: String, port: Int) = {
		Http().bindAndHandle(routes, host, port)
	}

}