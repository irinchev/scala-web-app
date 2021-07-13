package my.test.ws

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ddata.{GCounter, SelfUniqueAddress}
import akka.cluster.ddata.typed.scaladsl.DistributedData
import my.test.{IncomingMessage, SessionInit}
import akka.cluster.ddata.typed.scaladsl.Replicator._

class SessionStore {
  val actor: Behavior[IncomingMessage] = Behaviors.setup { context =>
//    implicit val node: SelfUniqueAddress = DistributedData(context.system).selfUniqueAddress
//    DistributedData.withReplicatorMessageAdapter[IncomingMessage, GCounter] { replicatorAdapter =>
//      replicatorAdapter.subscribe(key, InternalSubscribeResponse.apply)
//
//    }
    Behaviors.receiveMessage {
      case si: SessionInit =>
        context.log.info(s"SessionInit requested $si")
        Behaviors.same
    }
  }

}
