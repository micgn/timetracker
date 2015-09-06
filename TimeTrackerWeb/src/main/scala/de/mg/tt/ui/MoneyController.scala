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
package de.mg.tt.ui

import com.vaadin.ui._
import de.mg.tt.service.{TTMgmtGateway, FilterCriteria}
import de.mg.tt.ui.utils.ListenerUtils
import ListenerUtils._
import de.mg.tt.ui.compMoney.{MoneyCalcViewModel, MoneyCalcView}
import de.mg.tt.util.FloatHelper._

/**
 * Created by gnatz on 7/26/15.
 */
object MoneyController {

  def registerListeners(openBtn: Button, moneyVM: MoneyCalcViewModel,
                        filterCriteria: FilterCriteria, service: TTMgmtGateway) = {

    listener(openBtn, {
      val activities = service.findActivities(filterCriteria)
      val hours = activities.map(a => a.len).reduce((l1, l2) => l1 + l2) / 60.0
      MoneyCalcView.openMoneyCalcWindow(moneyVM, hours.toFloat)
    })

    listener[String](moneyVM.rate, s => {
      val rateF = toFloat(moneyVM.rate.getValue)
      val hoursF = toFloat(moneyVM.moneyHours.getValue)
      val taxF = 0.19 * rateF * hoursF
      moneyVM.tax.setValue(f"$taxF%1.2f €")
      val withoutF = rateF * hoursF
      moneyVM.moneyWithoutTax.setValue(f"$withoutF%1.2f €")
      val withF = rateF * hoursF * 1.19
      moneyVM.moneyWithTax.setValue(f"$withF%1.2f €")
    })
  }

}
