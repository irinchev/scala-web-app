package my.test.ws

import akka.NotUsed
import akka.actor.PoisonPill
import akka.actor.typed.SpawnProtocol.Spawn
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props, SpawnProtocol}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{get, handleWebSocketMessages, path}
import akka.http.scaladsl.server.Route
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Concat, Flow, Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import io.circe.Decoder
import io.circe.parser.decode
import my.test.{JsonCodecs, Service, SessionClose, SessionError, SessionInitWrapper, SessionOpened, WsMessage}
import org.slf4j.LoggerFactory

import java.util.UUID

object WebsocketHandler {

  private val log = LoggerFactory.getLogger(WebsocketHandler.getClass)

  def apply(sessionStore: ActorRef[WsMessage]): Route = {
    implicit val decoder: Decoder[WsMessage] = JsonCodecs.decoder
    path("ws") {
      get {
        handleWebSocketMessages(websocketHandler(sessionStore))
      }
    }
  }

  private def websocketHandler(sessionStore: ActorRef[WsMessage])(implicit decoder: Decoder[WsMessage]): Flow[Message, Message, Any] = {

    val id = UUID.randomUUID()

    val in = parseIncomingMessage
      .to(
        ActorSink.actorRef[WsMessage](
          ref = sessionStore,
          onCompleteMessage = SessionClose("Session Close"),
          onFailureMessage = { ex => SessionError(ex) }
        )
      )
    val out: Source[Message, Unit] = ActorSource
      .actorRef[WsMessage](
        completionMatcher = { case m: SessionClose => },
        failureMatcher = { case SessionError(ex) => ex },
        bufferSize = 10,
        OverflowStrategy.dropHead
      )
      .mapMaterializedValue(client => sessionStore ! SessionInitWrapper(client))
      .map {
        case m: WsMessage => TextMessage.Strict("")
        case SessionClose(_) | SessionError(_)   => TextMessage.Strict("")
      }
    Flow.fromSinkAndSource(in, out)
  }

  private def parseIncomingMessage(implicit decoder: Decoder[WsMessage]): Flow[Message, WsMessage, NotUsed] =
    Flow[Message].collect {
      case TextMessage.Strict(s) =>
        log.info(s"$s")
        decode[WsMessage](s).right.get
    }
}

case class SessionCreate(
    client: ActorRef[WsMessage]
)
