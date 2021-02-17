package my.test

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import my.test.dao.FilmDAO.findMovies
import scalikejdbc.config.DBs

import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.DefaultJsonProtocol

trait Protocols extends DefaultJsonProtocol {
  implicit val film = jsonFormat5(Film.apply)
}

trait Service extends Protocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor

  def config: Config
  val logger: LoggingAdapter

  val routes = {
    logRequestResult("akka-http-microservice") {
      pathPrefix("user") {
        (get & path(Segment)) { searchTerm =>
          complete {
            ToResponseMarshallable(findMovies())
          }
        }
      }
    }
  }
}

object Main extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)
  DBs.setupAll()
  Http()
    .newServerAt(config.getString("http.interface"), config.getInt("http.port"))
    .bindFlow(routes)
}
