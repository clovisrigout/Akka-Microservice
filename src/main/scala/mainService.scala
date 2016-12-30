import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContextExecutor

trait MainService {

	implicit val actorSystem: ActorSystem
	implicit val materializer: ActorMaterializer
	implicit def executor: ExecutionContextExecutor

	val list = List(1, 2, 3)

	val routes =
		path("") {
			get {
				complete {
					 "Hello World"
				}
			}
		}
}