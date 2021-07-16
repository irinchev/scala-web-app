package my.test.ws

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ddata.{GCounter, SelfUniqueAddress}
import akka.cluster.ddata.typed.scaladsl.DistributedData
import my.test.{DatagramMessage, SessionClose, SessionError, SessionInit, SessionInitWrapper, WsMessage}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

object SessionStore {
//  def apply(): Behavior[WsMessage] =
//    Behaviors.receive { (context, message) =>
////    implicit val node: SelfUniqueAddress = DistributedData(context.system).selfUniqueAddress
////    DistributedData.withReplicatorMessageAdapter[IncomingMessage, GCounter] { replicatorAdapter =>
////      replicatorAdapter.subscribe(key, InternalSubscribeResponse.apply)
////
////    }
//      message match {
//        case si: SessionInitWrapper =>
//          context.log.info(s"SessionInit requested $si")
//          Behaviors.same
//      }
//    }


  def openSession(): Behavior[WsMessage] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case SessionInitWrapper(ref) =>
        Behaviors.withTimers { timers =>
          timers.startTimerAtFixedRate(
            DatagramMessage("Now " + System.currentTimeMillis()),
            Duration.apply(1, TimeUnit.SECONDS)
          )
          onMessage(ref)
        }
      case SessionError(ex) =>
        ctx.log.warn("WebSocket failed", ex)
        Behaviors.stopped
      case SessionClose(ts) =>
        ctx.log.info("User closed connection")
        Behaviors.stopped
      case _ => Behaviors.same
    }
  }

  def onMessage(actorRef: ActorRef[WsMessage]): Behavior[WsMessage] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case SessionInitWrapper(_) => // shouldn't happen at this point
          Behaviors.same
        case SessionError(ex) =>
          ctx.log.warn("WebSocket failed", ex)
          Behaviors.stopped
        case SessionClose(ts) =>
          ctx.log.info("User closed connection")
          Behaviors.stopped
      }
    }
}
