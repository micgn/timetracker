/*
 * Copyright 2015 Michael Gnatz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mg.tt.util

import java.util.{Calendar, Date, GregorianCalendar}

/**
 * Created by gnatz on 1/6/15.
 */
object DateHelper {

  def dayOfMonth(date: Date = new Date()): Int = {
    get(date, java.util.Calendar.DAY_OF_MONTH)
  }

  def month0Based(date: Date = new Date()): Int = {
    get(date, java.util.Calendar.MONTH)
  }

  def month1Based(date: Date = new Date()): Int = {
    get(date, java.util.Calendar.MONTH) + 1
  }

  def weekOfYear(date: Date): Int = {
    get(date, java.util.Calendar.WEEK_OF_YEAR)
  }

  def year(date: Date = new Date()): Int = {
    get(date, java.util.Calendar.YEAR)
  }

  def isSameDay(d1: Date, d2: Date): Boolean =
    year(d1)==year(d2) && month0Based(d1) == month0Based(d2) && dayOfMonth(d1) == dayOfMonth(d2)

  private def get(date: Date, x: Int) = {
    val cal = java.util.Calendar.getInstance()
    cal.setTime(date)
    cal.get(x)
  }

  def dayStart(date: Date): Date = {
    val cal = new GregorianCalendar()
    cal.setTime(date)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  def dayEnd(date: Date): Date = {
    val cal = new GregorianCalendar()
    cal.setTime(date)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.getTime
  }

  def mondayOfWeek: Date = {
    val cal = new GregorianCalendar()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      cal.add(Calendar.DATE, -1)
    }
    cal.getTime
  }

  def beginOfWeek(anyDayInWeek: Date): Date = {
    val cal = new GregorianCalendar()
    cal.setTime(anyDayInWeek)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      cal.add(Calendar.DATE, -1)
    }
    cal.getTime
  }

  def endOfWeek(anyDayInWeek: Date): Date = {
    val start = beginOfWeek(anyDayInWeek);
    val cal = new GregorianCalendar()
    cal.setTime(start)
    cal.add(Calendar.DATE, 7)
    cal.getTime
  }


  def endOfMonth(date: Date = new Date()): Date = {
    val cal = new GregorianCalendar()
    cal.setTime(date)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))

    cal.getTime
  }

  def beginOfYear: Date = {
    val cal = new GregorianCalendar()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 1)
    cal.set(Calendar.MONTH, 0)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    cal.getTime
  }

  def formatOpt(minutes: Option[Long]): String =
    if (minutes.isDefined) format(minutes.get) else ""

  def format(totalMin: Long): String = {
    val days = totalMin / (60 * 8)
    val hours = (totalMin - days * 60 * 8) / 60
    val minutes = (totalMin - days * 60 * 8) % 60

    val dayStr = if (days > 0) days + "d " else ""
    val hourStr = if (hours > 0) hours + "h " else ""
    val minuteStr = if (minutes > 0) minutes + "m" else ""
    val dayHourMinStr = dayStr + hourStr + minuteStr

    val allHours = totalMin / 60.0
    val allHoursStr = if (allHours > 0) f" ($allHours%1.2f h)" else ""

    dayHourMinStr + allHoursStr
  }

  def getRightTimeDueToVaadinBug(d: Date): Date = {
    d
    /* only a bug if time zone are set differently on server and client
    val cal = new GregorianCalendar
    cal.setTime(d)
    cal.add(Calendar.HOUR, 1)
    cal.getTime */
  }
}
