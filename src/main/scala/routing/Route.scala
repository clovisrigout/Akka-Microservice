package routing

import akka.http.scaladsl.server

abstract class Route {

  def getRoute() : server.Route

}
