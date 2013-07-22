package actors

import akka.actor.{IndirectActorProducer, Props, Actor}
import com.google.inject.{Inject, Injector}
import models.Crucible
import play.api.Logger

class DistributingActor @Inject()(crucible: Crucible) extends Actor {
  def receive = {
    case t:DistributingActor.Tick => Logger.debug("got tick")
  }
}

object DistributingActor {
  def props(injector: Injector) = Props(classOf[Factory], injector)

  class Factory(injector: Injector) extends IndirectActorProducer {
    val actorClass = classOf[DistributingActor]
    def produce: Actor = {
      injector.getInstance(actorClass)
    }
  }

  case class Tick()
}