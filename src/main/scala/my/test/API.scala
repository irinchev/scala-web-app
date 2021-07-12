package my.test

import cats.implicits._
import io.circe.{Decoder, DecodingFailure, HCursor}
import io.circe.generic.auto._

sealed trait IncomingMessage

case class SessionInit(
    ts: String
) extends IncomingMessage

object JsonCodecs {

  implicit val decoder: Decoder[IncomingMessage] = (c: HCursor) =>
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
