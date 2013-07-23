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

class DistributingActorSpec extends Specification with NoTimeConversions with Mockito {

  "GatheringActor" should {
    "gather the latest data from crucible" in new AkkaSpecs2Context {
      within(1 second) {
        val crucible = mock[Crucible]
        crucible.reviews("mdrago") returns Enumerator.eof

        val underTest = TestActorRef(Props.create(classOf[DistributingActor], crucible))
        underTest ! DistributingActor.Tick()

        there was one(crucible).reviews("mdrago")
      }
    }
  }
}
