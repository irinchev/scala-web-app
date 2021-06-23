package my.test

import akka.actor.Address
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.management.scaladsl.AkkaManagement
import com.typesafe.config.{Config, ConfigFactory}
import scalikejdbc.config.DBs

import scala.concurrent.ExecutionContext

object Main extends BaseService("web-app") with Routes {
  DBs.setupAll()
  Http()
    .newServerAt(config.getString("http.interface"), config.getInt("http.port"))
    .bindFlow(routes)
}

class BaseService(val serviceName: String) extends App {

  import akka.management.cluster.bootstrap.ClusterBootstrap
  import akka.cluster.ClusterEvent._
  import akka.cluster.typed._

  val version = "1.0"

  implicit val config: Config = ConfigFactory.load().resolve()
  banner()

  implicit val system: ActorSystem[Nothing] = ActorSystem[Nothing](Behaviors.setup[Nothing] { context =>

    import akka.{ actor => classic }
    import akka.actor.typed.scaladsl.adapter._
    implicit val classicSystem: classic.ActorSystem = context.system.toClassic

    if ("cluster" == config.getString("akka.actor.provider")) {
      val clusterEventSubscriber = context.spawn(ClusterEventSubscriber(), "cluster-listener-actor")
      val cluster: Cluster = Cluster(context.system)
      cluster.subscriptions ! Subscribe(clusterEventSubscriber, classOf[MemberEvent])
      cluster.subscriptions ! Subscribe(clusterEventSubscriber, classOf[LeaderChanged])
      AkkaManagement.get(classicSystem).start()
      ClusterBootstrap.get(classicSystem).start()
    }

    Behaviors.empty
  }, s"$serviceName-system")
  implicit val executor: ExecutionContext = system.executionContext

  def banner(): Unit = {
    val mspLine = s"Compute Platform version $version"
    val svcVersion = if (config.hasPath("app.version")) config.getString("app.version") else ""
    val svcLine = if (svcVersion.nonEmpty) s"$serviceName $svcVersion" else serviceName
    val longest = List(mspLine, svcLine).maxBy(_.length)

    println()
    println(s"* $mspLine")
    println("* " + ("=" * longest.length))
    println(s"* $svcLine")
    println()
  }

  object ClusterEventSubscriber {

    var leader: Option[Address] = None
    def apply(): Behavior[Any] =
      Behaviors.receive { (context, message) =>
        message match {
          case CurrentClusterState =>
            context.log.debug("Current cluster state {}", message)
            Behaviors.same
          case MemberUp(member) =>
            context.log.info(s"member up: $member")
            leader.filter(_ == member.address) foreach { address =>
              context.log.info("Leader is now up...")
            }
            Behaviors.same
          case LeaderChanged(address) =>
            context.log.info(s"leader changed: $address")
            leader = address
            Behaviors.same
          case _ =>
            Behaviors.same
        }
      }
  }
}

case object ClusterState
