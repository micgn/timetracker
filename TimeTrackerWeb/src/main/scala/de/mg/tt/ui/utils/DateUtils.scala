package de.mg.tt.ui.utils

import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.Date

object DateUtils {

  def toDate(localDate: LocalDate): Date =
    Date.from(localDate.atStartOfDay(ZoneId.systemDefault).toInstant)

  def toDate(localDateTime: LocalDateTime): Date =
    Date.from(localDateTime.atZone(ZoneId.systemDefault).toInstant)

  def toLocalDate(date: Date): LocalDate =
    date.toInstant.atZone(ZoneId.systemDefault()).toLocalDate
}
