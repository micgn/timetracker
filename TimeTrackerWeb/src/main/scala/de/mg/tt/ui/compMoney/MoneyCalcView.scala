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
package de.mg.tt.ui.compMoney

import com.vaadin.ui.{Window, FormLayout, UI}
import de.mg.tt.ui.utils.LayoutUtils._

/**
 * Created by gnatz on 7/26/15.
 */
object MoneyCalcView {

  def openMoneyCalcWindow(m: MoneyCalcViewModel, hours: Float) = {
    m.moneyCalcW.setCaption("Money")
    m.moneyCalcW.setModal(true)
    m.moneyCalcW.setClosable(true)
    m.moneyCalcW.setResizable(false)
    m.moneyCalcW.setDraggable(true)
    m.moneyCalcW.center()

    val layout = new FormLayout
    layout.setSpacing(true)
    layout.setMargin(true)
    m.moneyCalcW.setContent(layout)

    m.moneyHours.setCaption("hours")
    //ui.moneyHours.setReadOnly(true)
    m.moneyHours.setValue(f"$hours%1.2f")
    layout.addComponent(m.moneyHours)

    m.rate.setCaption("rate (€)")
    m.rate.setMaxLength(5)
    layout.addComponent(m.rate)

    m.tax.setCaption("19% tax")
    m.tax.setValue("")
    //ui.tax.setReadOnly(true)
    layout.addComponent(m.tax)

    m.moneyWithoutTax.setCaption("mine")
    m.moneyWithoutTax.setValue("")
    //ui.moneyWithoutTax.setReadOnly(true)
    layout.addComponent(m.moneyWithoutTax)

    m.moneyWithTax.setCaption("tax included")
    m.moneyWithTax.setValue("")
    //ui.moneyWithTax.setReadOnly(true)
    layout.addComponent(m.moneyWithTax)

    btn(m.moneyCalcBtn, "calculate")
    layout.addComponent(m.moneyCalcBtn)

    UI.getCurrent.addWindow(m.moneyCalcW)
  }
}
