package my.test.ws

import akka.NotUsed
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{get, handleWebSocketMessages, path}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Concat, Flow, Sink, Source}
import io.circe.Decoder
import io.circe.parser.decode
import my.test.{IncomingMessage, JsonCodecs, Services}
import org.slf4j.LoggerFactory

object WebsocketHandler {

  private val log = LoggerFactory.getLogger(WebsocketHandler.getClass)

  def ws(): Route = {
    implicit val decoder: Decoder[IncomingMessage] = JsonCodecs.decoder
    path("ws") {
      get {
        handleWebSocketMessages(
//                    Flow[Message].map {
//                      case TextMessage.Strict(s) =>
//                        try {
//                          val incoming = decode[IncomingMessage](s)
//                        } catch {
//                          case e: Exception => log.error("Cannot parse JSON string", e)
//                        }
//                        TextMessage.Strict("Ok got the message.")
//                    }
          Flow.fromSinkAndSource(
            sink = parseIncomingMessage.to(Sink.foreach(log.info("{}", _))),
            //source = Source[Message](List(TextMessage("123123123123")))
            source = Source.maybe
          )
        )
      }
    }
  }

  private def parseIncomingMessage(implicit decoder: Decoder[IncomingMessage]): Flow[Message, IncomingMessage, NotUsed] =
    Flow[Message].collect {
      case TextMessage.Strict(s) =>
        log.info(s"$s")
        decode[IncomingMessage](s).right.get
    }
}