package utils

import akka.http.scaladsl.model.{HttpHeader, HttpResponse, StatusCodes, headers}
import akka.http.scaladsl.model.headers.{HttpOrigin, `Access-Control-Allow-Origin`}

class CustomHttpResponse(entity : Object) {

  def response : HttpResponse = {
    val allowOriginHeader = headers.RawHeader("Access-Control-Allow-Origin", "*")
    HttpResponse(StatusCodes.OK, headers = List(allowOriginHeader), entity = s"$entity")
  }

  val corsHeaders = List(
    headers.RawHeader("Access-Control-Allow-Origin", "*"),
    headers.RawHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE"),
    headers.RawHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
  )

}

object CustomHttpResponse {

  def apply(entity: Object): CustomHttpResponse = new CustomHttpResponse(entity)

}