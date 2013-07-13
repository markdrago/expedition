import actors.DistributingActor
import akka.actor.ActorRef
import com.google.inject.{Guice, AbstractModule}
import models._
import net.codingwell.scalaguice.ScalaModule
import play.api.Play.current
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.libs.Akka
import scala.concurrent.duration._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    //setupDistributingActor(system.distributingActor)
  }

  def setupDistributingActor(actor: ActorRef) {
    val frequency = Play.configuration.getInt("application.crucible.update_frequency").get
    Akka.system.scheduler.schedule(0.seconds, frequency.seconds, actor, DistributingActor.Tick())
  }

  private lazy val injector = Guice.createInjector(new ExpeditionSystem)

  override def getControllerInstance[A](clazz: Class[A]) = {
    injector.getInstance(clazz)
  }
}

class ExpeditionSystem extends AbstractModule with ScalaModule {
  def configure {
    bind[CrucibleWebService].to[CrucibleWebServiceImpl]
    bind[Crucible].to[CrucibleImpl]
  }
}
