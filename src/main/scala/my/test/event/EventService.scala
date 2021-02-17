package my.test.event

import akka.http.scaladsl.server.Directives.{complete, pathEndOrSingleSlash}
import akka.http.scaladsl.server.Route

class EventService {
  def route: Route = pathEndOrSingleSlash {
    complete("Welcome to websocket server")
  }
}
