package actors

import akka.actor.{IndirectActorProducer, Props, Actor}
import com.google.inject.{Inject, Injector}
import models.Crucible
import play.api.Logger

class DistributingActor @Inject()(crucible: Crucible) extends Actor {

  def receive = {
    case t:DistributingActor.Tick => tick
  }

  def tick {
    Logger.debug("got tick")
    val reviews = crucible.reviews
  }
  //on receive, make call to crucible
  //pass data on to time splitter
  //add split data to repository
  //notify selection of actors that there is new data
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