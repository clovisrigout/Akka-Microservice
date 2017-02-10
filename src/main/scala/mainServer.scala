package main.scala

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.io.StdIn
 
object MainServer extends App with MainService {
 
	override implicit val actorSystem = ActorSystem("server")
	override implicit val materializer = ActorMaterializer()
	override implicit val executor = actorSystem.dispatcher

	val config = ConfigFactory.load()
	val host = config.getString("http.host")
	val port = config.getInt("http.port")

	startServer(host, port)

	def startServer(host: String, port: Int) = {
		val bindingFuture = Http().bindAndHandle(routes, host, port)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => actorSystem.terminate()) // and shutdown when done
  }

}