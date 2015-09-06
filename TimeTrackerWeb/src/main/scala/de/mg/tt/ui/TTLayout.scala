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

import com.vaadin.server.Sizeable.Unit._
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._
import de.mg.tt.ui.utils.{ListenerUtils, LayoutUtils}
import LayoutUtils._
import ListenerUtils._
import de.mg.tt.ui.compAction.{ActionView, ActionViewModel}
import de.mg.tt.ui.compFilter.{FilterView, FilterViewModel}

/**
 * Created by gnatz on 12/28/14.
 */
class TTLayout(ui: TTUIController, filterVM: FilterViewModel, actionVM: ActionViewModel) {

  val TZ = "Europe/Berlin"

  def setupLayout() = {

    val split1 = new HorizontalSplitPanel
    split1.setSizeFull()
    split1.setSplitPosition(25, PERCENTAGE)

    val split2 = new VerticalSplitPanel
    split2.setSizeFull()
    split2.setSplitPosition(75, PERCENTAGE)

    val filterPanelLayout = vl()
    FilterView.setup(filterVM, filterPanelLayout)

    val actionsPanelLayout = hl(margin=false)
    ActionView.setup(actionVM, actionsPanelLayout)

    val tablePanelLayout = vl(margin=false, spacing=false)
    tablePanelLayout.setSizeFull()
    setupTablePanel(tablePanelLayout)

    split1.setFirstComponent(filterPanelLayout)
    split1.setSecondComponent(split2)
    split2.setFirstComponent(tablePanelLayout)
    split2.setSecondComponent(actionsPanelLayout)

    ui.setContent(split1)
  }

  private def setupTablePanel(p: AbstractLayout) = {

    ui.table.setSelectable(false)
    ui.table.setSizeFull()
    //ui.table.setSizeUndefined()
    p.addComponent(ui.table)
  }
}

object TTLayout {

  def question(q: String, execIfYes: => Unit) = {
    val w = new Window()
    w.setCaption(q)
    w.setModal(true)
    w.setClosable(false)
    w.setResizable(false)
    w.setDraggable(true)
    w.center()

    val btns = hl()
    val yes = new Button()
    btn(yes, "Yes")
    btns.addComponent(yes)

    val no = new Button
    btn(no, "No")
    btns.addComponent(no)

    w.setContent(btns)

    listener(yes, {
      execIfYes
      w.close()
    })

    listener(no, {
      w.close()
    })

    UI.getCurrent.addWindow(w)
  }

  def tableDayLabel(str: String, newWeek: Boolean, newDay: Boolean) =
    if (newWeek) new Label("<strong>" + str + "</strong>", ContentMode.HTML)
    else if (!newDay) new Label("<i>" + str + "</i>", ContentMode.HTML)
    else new Label(str, ContentMode.HTML)
}
