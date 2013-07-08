package service.hours

import org.joda.time._

//TODO: convert this whole thing to an object rather than a class?
//TODO: add support for holidays
class WorkHoursBetween(start: Instant, end: Instant) {

  private[hours] def days: Stream[LocalDate] = {
    def loop(d: LocalDate): Stream[LocalDate] =
      if (d.toDateMidnight.isAfter(end))
        Stream.empty
      else
        d #:: loop(d.plusDays(1))
    loop(new LocalDate(start))
  }

  private[hours] def weekdays: Stream[LocalDate] = days.filter(isWeekday)

  private[hours] def workHourIntervals: Stream[Interval] = weekdays.map(dayToWorkHourInterval)

  private[hours] def workHours: Double = {
    val entire_interval = new Interval(start, end)
    val overlapping_intervals = workHourIntervals.map(entire_interval.overlap(_))
    val total_millis = overlapping_intervals.foldLeft[Long](0)((acc, inter) => { acc + inter.toDuration.getMillis })
    total_millis / WorkHoursBetween.millisPerHour
  }

  private[this] def dayToWorkHourInterval(d: LocalDate): Interval =
    new Interval(
      d.toDateTime(WorkHoursBetween.startTime),
      d.toDateTime(WorkHoursBetween.endTime)
    )

  private[this] def isWeekday(d: LocalDate) =
    ! WorkHoursBetween.weekendDays.contains(d.dayOfWeek().get())
}

object WorkHoursBetween {
  //TODO move these constants in to the configuration rather than hard code them here
  val weekendDays = Set(DateTimeConstants.SATURDAY, DateTimeConstants.SUNDAY)
  val startTime = new LocalTime(10, 0)
  val endTime = new LocalTime(18, 0)
  val millisPerHour = 1000d * 60d * 60d

  def apply(start: String, end: String) =
    new WorkHoursBetween(Instant.parse(start), Instant.parse(end))
}