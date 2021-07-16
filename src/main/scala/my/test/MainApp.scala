package my.test

import akka.actor.typed.SpawnProtocol.Command
import akka.actor.typed.SpawnProtocol.Spawn
import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory.getLogger

import java.util.concurrent.TimeUnit

object MainApp extends App {
  val log = getLogger("main")
  implicit val config: Config               = ConfigFactory.load().resolve()
  implicit val system: ActorSystem[Command] = ActorSystem[Command](SpawnProtocol(), "WebSocketServer")
  implicit val timeout: Timeout             = Timeout(3, TimeUnit.SECONDS)
  implicit val scheduler: Scheduler         = system.scheduler

  system.ask[ActorRef[Nothing]](ref => Spawn[Nothing](Service(config, system), "root", Props.empty, ref))
}
