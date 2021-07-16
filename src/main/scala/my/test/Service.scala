package my.test

import akka.actor.Address
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.SpawnProtocol
import akka.actor.typed.SpawnProtocol.{Command, Spawn}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props, Scheduler}
import akka.cluster.ClusterEvent.{CurrentClusterState, LeaderChanged, MemberEvent, MemberUp}
import akka.cluster.typed.{Cluster, Subscribe}
import akka.http.scaladsl.Http
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.util.Timeout
import com.typesafe.config.Config
import my.test.ws.{SessionStore, WebsocketHandler}

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Service {

  def apply(implicit config: Config, system: ActorSystem[Command]): Behavior[Nothing] = Behaviors.setup[Nothing] { ctx =>
    setupAkkaCluster(config, ctx)
    setupHttpServer(config)
    Behaviors.empty
  }

  private def setupHttpServer(config: Config)(implicit system: ActorSystem[SpawnProtocol.Command]): Unit = {
    val routes = WebsocketHandler(sessionStore)
    Http()
      .newServerAt(config.getString("http.interface"), config.getInt("http.port"))
      .bindFlow(routes)
  }

  private def sessionStore(implicit system: ActorSystem[SpawnProtocol.Command]): ActorRef[WsMessage] = {
    implicit val timeout: Timeout     = Timeout(3, TimeUnit.SECONDS)
    implicit val scheduler: Scheduler = system.scheduler
    Await.result(
      system.ask[ActorRef[WsMessage]] { ref =>
        Spawn[WsMessage](SessionStore.openSession(), "sessionStore", props = Props.empty, replyTo = ref)
      },
      Duration.Inf
    )
  }

  private def setupAkkaCluster(config: Config, context:ActorContext[Nothing]): Unit = {
    import akka.actor.typed.scaladsl.adapter._
    import akka.{actor => classic}
    implicit val classicSystem: classic.ActorSystem = context.system.toClassic
    if ("cluster" == config.getString("akka.actor.provider")) {
      val clusterEventSubscriber = context.spawn(ClusterEventSubscriber(), "cluster-listener-actor")
      val cluster: Cluster = Cluster(context.system)
      cluster.subscriptions ! Subscribe(clusterEventSubscriber, classOf[MemberEvent])
      cluster.subscriptions ! Subscribe(clusterEventSubscriber, classOf[LeaderChanged])
      AkkaManagement.get(classicSystem).start()
      ClusterBootstrap.get(classicSystem).start()
    }
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
