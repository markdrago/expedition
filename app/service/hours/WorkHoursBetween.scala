package service.hours

import java.util
import org.joda.time._
import play.api.Play
import play.api.Play.current
import scala.collection.JavaConversions._

//TODO: add support for holidays
object WorkHoursBetween {
  val weekendDays = {
    val option = Play.configuration.getIntList("application.weekend_days")
    option.getOrElse(new util.LinkedList()).toSet
  }
  val holidays = {
    val option = Play.configuration.getStringList("application.holidays")
    option.getOrElse(new util.LinkedList()).toSet.map(LocalDate.parse)
  }
  val startTime = LocalTime.parse(Play.configuration.getString("application.start_time").get)
  val endTime = LocalTime.parse(Play.configuration.getString("application.end_time").get)

  def apply(start: String, end: String): Double = apply(Instant.parse(start), Instant.parse(end))

  def apply(start: Instant, end: Instant): Double = {
    def days: Stream[LocalDate] = dayStream(start, end)
    def isWeekday(d: LocalDate) = ! weekendDays.contains(d.dayOfWeek().get())
    def isNotHoliday(d: LocalDate) = ! holidays.contains(d)
    def workdays: Stream[LocalDate] = days.filter(isWeekday).filter(isNotHoliday)
    def dayToWorkHourInterval(d: LocalDate) = new Interval(d.toDateTime(startTime), d.toDateTime(endTime))
    def workHourIntervals: Stream[Interval] = workdays.map(dayToWorkHourInterval)

    val entireInterval = new Interval(start, end)
    val overlappingIntervals = workHourIntervals.map(entireInterval.overlap(_))
    val totalMillis = overlappingIntervals.foldLeft[Long](0)((acc, inter) => { acc + inter.toDuration.getMillis })
    totalMillis / DateTimeConstants.MILLIS_PER_HOUR.toDouble
  }

  def dayStream(start: Instant, end: Instant) = {
    def loop(d: LocalDate): Stream[LocalDate] =
      if (d.toDateMidnight.isAfter(end))
        Stream.empty
      else
        d #:: loop(d.plusDays(1))
    loop(new LocalDate(start))
  }
}