package my.test

import akka.actor.ActorSystem

object Services {

  def init(implicit system: ActorSystem): Unit = {

  }

  def dispatch(incoming: Either[Error, IncomingMessage]): Unit = {

  }

  def onError(): Unit = {

  }
}
