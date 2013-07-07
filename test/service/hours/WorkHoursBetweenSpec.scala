package service.hours

import org.specs2.mutable._
import org.joda.time.{Interval, LocalDate, Instant}

class WorkHoursBetweenSpec extends Specification {

  "WorkHoursBetween.days" should {
    "contain the right set of days" in {
      val hours = WorkHoursBetween("2013-07-07T07:03:00", "2013-07-10T18:30:00")
      hours.days.toSeq should equalTo(localDateSeq("2013-07-07", "2013-07-08", "2013-07-09", "2013-07-10"))
    }
  }

  "WorkHoursBetween.weekdays" should {
    "skip over weekend days" in {
      val hours = WorkHoursBetween("2013-07-05T07:03:00", "2013-07-08T18:30:00")
      hours.weekdays.toSeq should equalTo(localDateSeq("2013-07-05", "2013-07-08"))
    }
  }

  "WorkHoursBetween.workHourIntervals" should {
    "convert days to intervals" in {
      val hours = WorkHoursBetween("2013-07-08T07:03:00", "2013-07-10T18:30:00")
      hours.workHourIntervals.toSeq should equalTo(workHoursSeq("2013-07-08", "2013-07-09", "2013-07-10"))
    }
  }

  "WorkHoursBetween.workHours" should {
    "return the correct number of hours for single day all within workday" in {
      val hours = WorkHoursBetween("2013-07-08T11:00:00", "2013-07-08T12:00:00")
      hours.workHours should equalTo(1d)
    }

    "return the correct number of hours when interval starts before workday" in {
      val hours = WorkHoursBetween("2013-07-08T09:00:00", "2013-07-08T12:00:00")
      hours.workHours should equalTo(2d)
    }

    "return the correct number of hours when interval ends after workday" in {
      val hours = WorkHoursBetween("2013-07-08T15:00:00", "2013-07-08T20:00:00")
      hours.workHours should equalTo(3d)
    }

    "return the correct number of hours when spans multiple days" in {
      val hours = WorkHoursBetween("2013-07-08T15:00:00", "2013-07-09T12:30:00")
      hours.workHours should equalTo(5.5)
    }

    "return the correct number of hours when spans a weekend" in {
      val hours = WorkHoursBetween("2013-07-12T15:00:00", "2013-07-15T11:30:00")
      hours.workHours should equalTo(4.5)
    }
  }

  private def localDateSeq(dayStrings: String*): Seq[LocalDate] =
    dayStrings.map(LocalDate.parse(_))

  private def workHoursSeq(dayStrings: String*): Seq[Interval] =
    localDateSeq(dayStrings:_*).map(d =>
      new Interval(
        d.toDateTime(WorkHoursBetween.startTime),
        d.toDateTime(WorkHoursBetween.endTime)
      )
    )
}
