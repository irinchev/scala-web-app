package my.test

import akka.actor.typed.ActorRef
import cats.implicits._
import io.circe.{Decoder, DecodingFailure, HCursor}
import io.circe.generic.auto._

sealed trait WsMessage

case class SessionInit(
    ts: String
) extends WsMessage

case class SessionInitWrapper(
    client: ActorRef[WsMessage]
) extends WsMessage

case class SessionOpened(
    id: String
) extends WsMessage

case class SessionClose(
    ts: String
) extends WsMessage

case class SessionError(
    ex: Throwable
) extends WsMessage

case class DatagramMessage(
    data: String
) extends WsMessage

object JsonCodecs {

  implicit val decoder: Decoder[WsMessage] = (c: HCursor) =>
    for {
      t <- c.get[String]("mType")
      result <- t match {
        case "SessionInit" => c.as[SessionInit]
        case s             => DecodingFailure(s"Decoding failure $s", c.history).asLeft
      }
    } yield result
}

case class Film(
    title: String,
    description: String,
    language: String,
    length: Int,
    releaseYear: Int
)

case class User(
    user: String,
    name: String
)

case class RequestTwo(
    ccy: String,
    tenor: String
)
