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
package de.mg.tt.ui.controller

import java.time.LocalDate
import java.util
import java.util.Date

import de.mg.holidays.HolidayAPI
import de.mg.holidays.model.Holiday
import de.mg.tt.ui.compFilter.FilterViewModel
import de.mg.tt.ui.utils.DateUtils
import de.mg.tt.util.DateHelper._
import org.vaadin.addons.tuningdatefield.event.MonthChangeEvent
import org.vaadin.addons.tuningdatefield.{CellItemCustomizerAdapter, TuningDateField}

import scala.collection.JavaConverters._

/**
  * Created by gnatz on 7/26/15.
  */
class DaysController(filterVM: FilterViewModel) {

  private val holidayApi = new HolidayAPI

  // cache
  private var currentHolidays: util.List[Holiday] = _


  def init() = {
    val now = new Date()
    currentHolidays = holidayApi.get(year(now), month1Based(now))

    {
      val y = year(mondayOfWeek)
      val m = month1Based(mondayOfWeek)
      filterVM.workingDays.setValue(String.valueOf(holidayApi.getAmountWorkingDays(y, m)))
      filterVM.passedWorkingDays.setValue(String.valueOf(holidayApi.getPassedWorkingDay(y, m)))
    }

    filterVM.filterFrom.setCellItemCustomizer(new MyCellItemCustomizer)
    class MyCellItemCustomizer extends CellItemCustomizerAdapter {
      override def getTooltip(date: LocalDate, tuningDateField: TuningDateField): String = {
        val ho = getHoliday(DateUtils.toDate(date.atStartOfDay()))
        if (ho.nonEmpty) ho.get.description else ""
      }

      override def getStyle(date: LocalDate, tuningDateField: TuningDateField): String = {
        if (getHoliday(DateUtils.toDate(date.atStartOfDay())).nonEmpty)
          "holiday"
        else
          null
      }
    }

    filterVM.filterFrom.addMonthChangeListener((monthChangeEvent: MonthChangeEvent) => {
      val selected = monthChangeEvent.getYearMonth
      val selectedYear = selected.getYear
      val selectedMonth = selected.getMonthValue
      filterVM.workingDays.setValue(String.valueOf(holidayApi.getAmountWorkingDays(selectedYear, selectedMonth)))
      filterVM.passedWorkingDays.setValue(String.valueOf(holidayApi.getPassedWorkingDay(selectedYear, selectedMonth)))
      // initialize cache
      currentHolidays = holidayApi.get(selectedYear, selectedMonth)
    })
  }

  private def getHoliday(date: Date): Option[Holiday] = {
    currentHolidays.asScala.find(ho => ho.equalsDate(date))
  }
}
