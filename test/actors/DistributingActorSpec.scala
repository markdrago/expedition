package actors

import akka.actor.Props
import akka.testkit.TestActorRef
import models.Crucible
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.time.NoTimeConversions
import play.api.libs.iteratee.Enumerator
import scala.concurrent.duration._
import util.AkkaSpecs2Context

class DistributingActorSpec extends Specification with NoTimeConversions {

  private trait Fixture extends AkkaSpecs2Context with Mockito {
    val crucible = mock[Crucible]
    val underTest = TestActorRef(Props.create(classOf[DistributingActor], crucible))
  }

  "DistributingActor" should {
    "gather the latest data from crucible" in new Fixture {
      within(1 second) {
        crucible.reviews returns Enumerator.eof

        underTest ! DistributingActor.Tick()

        there was one(crucible).reviews
      }
    }
  }
}
