package my.test

import akka.http.scaladsl.server.Route
import my.test.ws.WebsocketHandler

trait Routes {
  val routes: Route = WebsocketHandler.ws()
}
