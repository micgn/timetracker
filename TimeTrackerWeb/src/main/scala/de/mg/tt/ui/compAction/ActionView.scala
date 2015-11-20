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
package de.mg.tt.ui.compAction

import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import com.vaadin.server.Sizeable
import com.vaadin.ui.AbstractLayout
import de.mg.tt.ui.utils.LayoutUtils
import LayoutUtils._

/**
 * Created by gnatz on 7/26/15.
 */
object ActionView {

  def setup(m: ActionViewModel, p: AbstractLayout) = {

    val selectContP = hl(border=true)
    selectContP.setCaption("selection")
    p.addComponent(selectContP)

    val selectP = vl(border=true)
    btn(m.allBtn, "all", small=true)
    selectP.addComponent(m.allBtn)
    btn(m.noneBtn, "none", small=true)
    selectP.addComponent(m.noneBtn)
    btn(m.invertBtn, "invert", small=true)
    selectP.addComponent(m.invertBtn)
    selectContP.addComponent(selectP)

    val selectActP = vl(margin=false)
    val addCatP = hl(margin=false)
    m.catChooser.setNullSelectionAllowed(true);
    addCatP.addComponent(m.catChooser)
    btn(m.addCatBtn, "add")
    m.addCatBtn.setWidth(5, Sizeable.Unit.EM)
    m.addCatBtn.setEnabled(false)
    addCatP.addComponent(m.addCatBtn)

    m.catChooser.addValueChangeListener(new ValueChangeListener {
      override def valueChange(valueChangeEvent: ValueChangeEvent): Unit =
        m.addCatBtn.setEnabled(m.catChooser.getValue != null)
    })

    btn(m.deleteBtn, "delete")

    selectActP.addComponent(m.deleteBtn)
    selectActP.addComponent(addCatP)
    selectContP.addComponent(selectActP)

    val catContP = vl(border=true)
    catContP.setCaption("category")
    p.addComponent(catContP)
    btn(m.newCatBtn, "new")
    catContP.addComponent(m.newCatBtn)

    val delCatP = hl()
    delCatP.setMargin(false)
    m.delCatChooser.setNullSelectionAllowed(true);
    delCatP.addComponent(m.delCatChooser)
    btn(m.delCatBtn, "delete")
    m.delCatBtn.setWidth(5, Sizeable.Unit.EM)
    m.delCatBtn.setEnabled(false)
    delCatP.addComponent(m.delCatBtn)
    m.delCatChooser.addValueChangeListener(new ValueChangeListener {
      override def valueChange(valueChangeEvent: ValueChangeEvent): Unit =
        m.delCatBtn.setEnabled(m.delCatChooser.getValue != null)
    })
    catContP.addComponent(delCatP)

    val actContP = hl(border=true)
    actContP.setCaption("activity")
    p.addComponent(actContP)
    val actP = hl(margin=false)
    actContP.addComponent(actP)
    btn(m.newBtn, "new", important=true)
    actP.addComponent(m.newBtn)

    val saveContP = hl(border=true)
    saveContP.setCaption("save")
    p.addComponent(saveContP)
    val saveP = vl(margin=false)
    saveContP.addComponent(saveP)
    btn(m.saveBtn, "save", important=true)
    saveP.addComponent(m.saveBtn)
    btn(m.revertBtn, "revert")
    saveP.addComponent(m.revertBtn)

    val exportContP = vl(border=true)
    exportContP.setCaption("...")
    p.addComponent(exportContP)
    val exportP = vl(margin=false)
    exportContP.addComponent(exportP)
    btn(m.exportBtn, "export")
    exportP.addComponent(m.exportBtn)
    btn(m.exportPerDayBtn, "day export")
    exportP.addComponent(m.exportPerDayBtn)
    btn(m.openMoneyCalc, "calculate")
    exportP.addComponent(m.openMoneyCalc)
  }
}
