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
package de.mg.tt.ui.compActivity

import java.util.{Date, Locale, TimeZone}

import com.vaadin.server.Sizeable
import com.vaadin.shared.ui.datefield.Resolution._
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.{Button, FormLayout, Label, UI}
import de.mg.tt.ui.utils.{ListenerUtils, LayoutUtils}
import LayoutUtils._
import ListenerUtils._

/**
 * Created by gnatz on 7/26/15.
 */
object ActivityView {

  def openActivityWindow(m: ActivityViewModel) = {

    val df = "EE dd.MM.yy HH:mm"

    m.activityW.setCaption("Activity")
    m.activityW.setModal(true)
    m.activityW.setClosable(false)
    m.activityW.setResizable(false)
    m.activityW.setDraggable(true)
    m.activityW.center()

    val layout = new FormLayout
    layout.setSpacing(true)
    layout.setMargin(true)
    m.activityW.setContent(layout)

    m.actFrom.setCaption("from")
    m.actFrom.setResolution(MINUTE)
    m.actFrom.setTimeZone(TimeZone.getTimeZone(TZ))
    m.actFrom.setLocale(Locale.GERMANY)
    m.actFrom.setDateFormat(df)
    layout.addComponent(m.actFrom)
    listener[Date](m.actFrom, d => {
      m.actTo.setValue(d)
    })

    m.actTo.setCaption("to")
    m.actTo.setResolution(MINUTE)
    m.actTo.setTimeZone(TimeZone.getTimeZone(TZ))
    m.actTo.setLocale(Locale.GERMANY)
    m.actTo.setDateFormat(df)
    layout.addComponent(m.actTo)

    m.actDescr.setCaption("description")
    m.actDescr.setMaxLength(255)
    m.actDescr.setWidth(50, Sizeable.Unit.EM)
    layout.addComponent(m.actDescr)

    m.actLongDescr.setCaption("long")
    m.actLongDescr.setHeight(5, Sizeable.Unit.EM)
    m.actLongDescr.setWidth(50, Sizeable.Unit.EM)
    layout.addComponent(m.actLongDescr)

    m.actCats.setCaption("categories")
    m.actCats.setNullSelectionAllowed(true)
    m.actCats.setMultiSelect(true)
    m.actCats.setLeftColumnCaption("available")
    m.actCats.setRightColumnCaption("selected")
    layout.addComponent(m.actCats)

    layout.addComponent(new Label("<div class=\"v-spacing\"></div>", ContentMode.HTML))

    val btns = hl(margin=false)
    btn(m.actAddBtn, "Add", important=true)
    btns.addComponent(m.actAddBtn)

    btn(m.actAddSaveBtn, "Add & Save", important=true)
    btns.addComponent(m.actAddSaveBtn)

    val cancel = new Button
    btn(cancel, "Cancel")
    btns.addComponent(cancel)
    listener(cancel, {
      m.activityW.close()
    })

    layout.addComponent(btns)

    UI.getCurrent.addWindow(m.activityW)
  }
}
