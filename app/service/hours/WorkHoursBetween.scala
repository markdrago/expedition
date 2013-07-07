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

  //TODO: only convert to hours after accumulating integers of millis to avoid division weirdness
  private[hours] def workHours: Double = {
    val total = new Interval(start, end)
    workHourIntervals
      .map(total.overlap(_))
      .foldLeft[Double](0d)((acc: Double, inter: Interval) => {
        acc + inter.toDuration.getMillis / WorkHoursBetween.millisPerHour
      })
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
  val weekendDays = Set(DateTimeConstants.SATURDAY, DateTimeConstants.SUNDAY)
  val startTime = new LocalTime(10, 0)
  val endTime = new LocalTime(18, 0)
  val millisPerHour = 1000d * 60d * 60d

  def apply(start: String, end: String) =
    new WorkHoursBetween(Instant.parse(start), Instant.parse(end))
}