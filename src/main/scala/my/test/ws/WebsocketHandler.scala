package my.test.ws

import akka.NotUsed
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{
  complete,
  extractWebSocketUpgrade,
  get,
  handleWebSocketMessages,
  path
}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.typed.scaladsl.ActorSink
import org.slf4j.LoggerFactory

object WebsocketHandler {

  private val log = LoggerFactory.getLogger(WebsocketHandler.getClass)

  def ws(): Route = {
    val svc = Flow[Message].map { m =>
      log.info(s"$m")
      m
    }
    path("ws") {
      get {
        handleWebSocketMessages({
          Flow[Message].map {
            case TextMessage.Strict(s) =>
              log.info(s)
              TextMessage.Strict("Ok got the message.")
          }
        })
//        extractWebSocketUpgrade { upgrade =>
//        log.info(s"$upgrade")
//          complete(upgrade.handleMessages(svc))
//        }
      }
    }
  }
}
