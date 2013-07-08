package service.hours

import org.specs2.mutable._

class WorkHoursBetweenSpec extends Specification {

  "WorkHoursBetween" should {
    "return the correct number of hours for single day all within workday" in {
      val hours = WorkHoursBetween("2013-07-08T11:00:00", "2013-07-08T12:00:00")
      hours should equalTo(1d)
    }

    "return the correct number of hours when interval starts before workday" in {
      val hours = WorkHoursBetween("2013-07-08T09:00:00", "2013-07-08T12:00:00")
      hours should equalTo(2d)
    }

    "return the correct number of hours when interval ends after workday" in {
      val hours = WorkHoursBetween("2013-07-08T15:00:00", "2013-07-08T20:00:00")
      hours should equalTo(3d)
    }

    "return the correct number of hours when spans multiple days" in {
      val hours = WorkHoursBetween("2013-07-08T15:00:00", "2013-07-09T12:30:00")
      hours should equalTo(5.5)
    }

    "return the correct number of hours when spans a weekend" in {
      val hours = WorkHoursBetween("2013-07-12T15:00:00", "2013-07-15T11:30:00")
      hours should equalTo(4.5)
    }
  }
}
