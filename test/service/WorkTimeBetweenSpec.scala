package service

import org.specs2.mutable._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import scala.collection.JavaConversions._

class WorkTimeBetweenSpec extends Specification {

  private val fakeApp = FakeApplication(
    additionalConfiguration = Map(
      "application.start_time" -> "10:00",
      "application.end_time" -> "18:00",
      "application.weekend_days" -> seqAsJavaList(Seq(6, 7)),
      "application.holidays" -> seqAsJavaList(Seq("2013-07-04"))
    )
  )

  "WorkTimeBetween" should {
    "return the correct number of minutes for single day all within workday" in {
      running(fakeApp) {
        val minutes = WorkTimeBetween("2013-07-08T11:00:00", "2013-07-08T12:00:00")
        minutes should equalTo(60)
      }
    }

    "return the correct number of minutes when interval starts before workday" in {
      running(fakeApp) {
        val minutes = WorkTimeBetween("2013-07-08T09:00:00", "2013-07-08T12:00:00")
        minutes should equalTo(120)
      }
    }

    "return the correct number of minutes when interval ends after workday" in {
      running(fakeApp) {
        val minutes = WorkTimeBetween("2013-07-08T15:00:00", "2013-07-08T20:00:00")
        minutes should equalTo(180)
      }
    }

    "return the correct number of minutes when spanning multiple days" in {
      running(fakeApp) {
        val minutes = WorkTimeBetween("2013-07-08T15:00:00", "2013-07-09T12:30:00")
        minutes should equalTo(330)
      }
    }

    "return the correct number of minutes when spanning a weekend" in {
      running(fakeApp) {
        val minutes = WorkTimeBetween("2013-07-12T15:00:00", "2013-07-15T11:30:00")
        minutes should equalTo(270)
      }
    }

    "return the correct number of minutes when spanning a holiday" in {
      running(fakeApp) {
        val minutes = WorkTimeBetween("2013-07-03T15:00:00", "2013-07-05T11:30:00")
        minutes should equalTo(270)
      }
    }
  }
}
