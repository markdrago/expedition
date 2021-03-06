package service

import org.specs2.mutable._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import scala.collection.JavaConversions._

class WorkTimeBetweenSpec extends Specification {

  private def fakeApp = FakeApplication(
    additionalConfiguration = Map(
      "application.start_time" -> "10:00",
      "application.end_time" -> "18:00",
      "application.weekend_days" -> seqAsJavaList(Seq(6, 7)),
      "application.holidays" -> seqAsJavaList(Seq("2013-07-04"))
    )
  )

  "WorkTimeBetween" should {
    "return the correct number of seconds for single day all within workday" in {
      running(fakeApp) {
        val seconds = WorkTimeBetween("2013-07-08T11:00:00", "2013-07-08T12:00:00")
        seconds should equalTo(60 * 60)
      }
    }

    "return the correct number of seconds when interval starts before workday" in {
      running(fakeApp) {
        val seconds = WorkTimeBetween("2013-07-08T09:00:00", "2013-07-08T12:00:00")
        seconds should equalTo(120 * 60)
      }
    }

    "return the correct number of seconds when interval ends after workday" in {
      running(fakeApp) {
        val seconds = WorkTimeBetween("2013-07-08T15:00:00", "2013-07-08T20:00:00")
        seconds should equalTo(180 * 60)
      }
    }

    "return the correct number of seconds when spanning multiple days" in {
      running(fakeApp) {
        val seconds = WorkTimeBetween("2013-07-08T15:00:00", "2013-07-09T12:30:00")
        seconds should equalTo(330 * 60)
      }
    }

    "return the correct number of seconds when spanning a weekend" in {
      running(fakeApp) {
        val seconds = WorkTimeBetween("2013-07-12T15:00:00", "2013-07-15T11:30:00")
        seconds should equalTo(270 * 60)
      }
    }

    "return the correct number of seconds when spanning a holiday" in {
      running(fakeApp) {
        val seconds = WorkTimeBetween("2013-07-03T15:00:00", "2013-07-05T11:30:00")
        seconds should equalTo(270 * 60)
      }
    }
  }
}
