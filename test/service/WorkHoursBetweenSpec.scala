package service

import org.specs2.mutable._
import play.api.test.FakeApplication
import play.api.test.Helpers._
import scala.collection.JavaConversions._
import service.WorkHoursBetween

class WorkHoursBetweenSpec extends Specification {

  private val fakeApp = FakeApplication(
    additionalConfiguration = Map(
      "application.start_time" -> "10:00",
      "application.end_time" -> "18:00",
      "application.weekend_days" -> seqAsJavaList(Seq(6, 7)),
      "application.holidays" -> seqAsJavaList(Seq("2013-07-04"))
    )
  )

  "WorkHoursBetween" should {
    "return the correct number of hours for single day all within workday" in {
      running(fakeApp) {
        val hours = WorkHoursBetween("2013-07-08T11:00:00", "2013-07-08T12:00:00")
        hours should equalTo(1d)
      }
    }

    "return the correct number of hours when interval starts before workday" in {
      running(fakeApp) {
        val hours = WorkHoursBetween("2013-07-08T09:00:00", "2013-07-08T12:00:00")
        hours should equalTo(2d)
      }
    }

    "return the correct number of hours when interval ends after workday" in {
      running(fakeApp) {
        val hours = WorkHoursBetween("2013-07-08T15:00:00", "2013-07-08T20:00:00")
        hours should equalTo(3d)
      }
    }

    "return the correct number of hours when spanning multiple days" in {
      running(fakeApp) {
        val hours = WorkHoursBetween("2013-07-08T15:00:00", "2013-07-09T12:30:00")
        hours should equalTo(5.5)
      }
    }

    "return the correct number of hours when spanning a weekend" in {
      running(fakeApp) {
        val hours = WorkHoursBetween("2013-07-12T15:00:00", "2013-07-15T11:30:00")
        hours should equalTo(4.5)
      }
    }

    "return the correct number of hours when spanning a holiday" in {
      running(fakeApp) {
        val hours = WorkHoursBetween("2013-07-03T15:00:00", "2013-07-05T11:30:00")
        hours should equalTo(4.5)
      }
    }
  }
}
