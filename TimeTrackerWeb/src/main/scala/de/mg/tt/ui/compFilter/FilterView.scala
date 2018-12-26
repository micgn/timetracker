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
package de.mg.tt.ui.compFilter

import java.util.{Locale, TimeZone}

import com.vaadin.server.Sizeable
import com.vaadin.ui.AbstractLayout
import com.vaadin.v7.shared.ui.datefield.Resolution
import com.vaadin.v7.shared.ui.label.ContentMode
import com.vaadin.v7.ui.Label
import de.mg.tt.ui.utils.LayoutUtils
import de.mg.tt.ui.utils.LayoutUtils._
import org.vaadin.addons.tuningdatefield.widgetset.client.ui.calendar.CalendarResolution

/**
 * Created by gnatz on 7/26/15.
 */
object FilterView {

  def setup(m: FilterViewModel, p: AbstractLayout): Unit = {
    val monthP = hl(margin = false)

    p.addComponent(monthP)

    m.filterFrom.setCaption("from")
    m.filterFrom.setCalendarResolution(CalendarResolution.DAY)
    m.filterFrom.setLocale(Locale.ENGLISH)
    m.filterFrom.setWeekendDisabled(false)
    m.filterFrom.setDisplayFixedNumberOfDayRows(true)
    monthP.addComponent(m.filterFrom)

    val daysP = vl(margin = false)
    monthP.addComponent(daysP)

    m.workingDays.setCaption("working days")
    m.workingDays.setMaxLength(3)
    m.workingDays.setWidth(3, Sizeable.Unit.EM)
    m.workingDays.setEnabled(false)
    daysP.addComponent(m.workingDays)

    m.passedWorkingDays.setCaption("passed")
    m.passedWorkingDays.setMaxLength(3)
    m.passedWorkingDays.setWidth(3, Sizeable.Unit.EM)
    m.passedWorkingDays.setEnabled(false)
    daysP.addComponent(m.passedWorkingDays)

    m.filterTo.setCaption("to")
    m.filterTo.setResolution(Resolution.DAY)
    m.filterTo.setImmediate(true)
    m.filterTo.setTimeZone(TimeZone.getTimeZone(LayoutUtils.TZ))
    m.filterTo.setLocale(Locale.ENGLISH)
    p.addComponent(m.filterTo)

    p.addComponent(new Label("<div class=\"v-spacing\"></div>", ContentMode.HTML))

    m.filterCat.setRows(10)
    m.filterCat.setNullSelectionAllowed(true)
    m.filterCat.setMultiSelect(true)
    m.filterCat.setImmediate(true)
    m.filterCat.setLeftColumnCaption("available")
    m.filterCat.setRightColumnCaption("selected")
    p.addComponent(m.filterCat)

    p.addComponent(new Label("<div class=\"v-spacing\"></div>", ContentMode.HTML))

    val filterBtnsP = hl(margin = false)
    btn(m.filterBtn, "filter", important = true)
    btn(m.resetSessionBtn, "reset session")
    filterBtnsP.addComponent(m.filterBtn)
    filterBtnsP.addComponent(m.resetSessionBtn)
    p.addComponent(filterBtnsP)
  }
}
