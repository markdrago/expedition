package service.hours

import org.joda.time._

//TODO: add support for holidays
object WorkHoursBetween {
  //TODO move these constants in to the configuration rather than hard code them here
  val weekendDays = Set(DateTimeConstants.SATURDAY, DateTimeConstants.SUNDAY)
  val startTime = new LocalTime(10, 0)
  val endTime = new LocalTime(18, 0)
  val millisPerHour = 1000d * 60d * 60d

  def apply(start: String, end: String): Double = apply(Instant.parse(start), Instant.parse(end))

  def apply(start: Instant, end: Instant): Double = {
    def days: Stream[LocalDate] = dayStream(start, end)
    def isWeekday(d: LocalDate) = ! weekendDays.contains(d.dayOfWeek().get())
    def weekdays: Stream[LocalDate] = days.filter(isWeekday)
    def dayToWorkHourInterval(d: LocalDate) = new Interval(d.toDateTime(startTime), d.toDateTime(endTime))
    def workHourIntervals: Stream[Interval] = weekdays.map(dayToWorkHourInterval)

    val entire_interval = new Interval(start, end)
    val overlapping_intervals = workHourIntervals.map(entire_interval.overlap(_))
    val total_millis = overlapping_intervals.foldLeft[Long](0)((acc, inter) => { acc + inter.toDuration.getMillis })
    total_millis / millisPerHour
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