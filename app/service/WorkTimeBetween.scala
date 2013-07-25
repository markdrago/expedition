package service

import org.joda.time._
import play.api.Play
import play.api.Play.current
import scala.collection.JavaConversions._

object WorkTimeBetween {
  val weekendDays =
    Play.configuration.getIntList("application.weekend_days")
      .map(_.toSet).getOrElse(Set.empty)

  val holidays =
    Play.configuration.getStringList("application.holidays")
      .map(_.toSet).getOrElse(Set.empty)
      .map(LocalDate.parse)

  val startTime = LocalTime.parse(Play.configuration.getString("application.start_time").getOrElse("09:00"))
  val endTime = LocalTime.parse(Play.configuration.getString("application.end_time").getOrElse("17:00"))

  def apply(start: String, end: String): Long = apply(Instant.parse(start), Instant.parse(end))

  def apply(start: Instant, end: Instant): Long = {
    def days: Stream[LocalDate] = dayStream(start, end)
    def isWeekday(d: LocalDate) = ! weekendDays.contains(d.dayOfWeek().get())
    def isNotHoliday(d: LocalDate) = ! holidays.contains(d)
    def workdays: Stream[LocalDate] = days.filter(isWeekday).filter(isNotHoliday)
    def dayToWorkHourInterval(d: LocalDate) = new Interval(d.toDateTime(startTime), d.toDateTime(endTime))
    def workHourIntervals: Stream[Interval] = workdays.map(dayToWorkHourInterval)

    val entireInterval = new Interval(start, end)
    val overlappingIntervals = workHourIntervals.map(entireInterval.overlap(_))
    val totalMillis = overlappingIntervals.foldLeft[Long](0)((acc, inter) => { acc + inter.toDuration.getMillis })
    totalMillis / DateTimeConstants.MILLIS_PER_SECOND
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