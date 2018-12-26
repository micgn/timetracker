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
package de.mg.tt.ui.compCategory

import com.vaadin.server.Sizeable
import com.vaadin.ui.{Button, FormLayout, UI}
import de.mg.tt.ui.utils.LayoutUtils._
import de.mg.tt.ui.utils.ListenerUtils._

/**
 * Created by gnatz on 7/26/15.
 */
object CategoryView {

  def openCategoryWindow(m: CategoryViewModel): Unit = {
    m.categoryW.setCaption("Category")
    m.categoryW.setModal(true)
    m.categoryW.setClosable(false)
    m.categoryW.setResizable(false)
    m.categoryW.setDraggable(true)
    m.categoryW.center()

    val layout = new FormLayout
    layout.setSpacing(true)
    layout.setMargin(true)
    m.categoryW.setContent(layout)

    m.catName.setCaption("Name")
    m.catName.setMaxLength(50)
    m.catName.setWidth(50, Sizeable.Unit.EM)
    layout.addComponent(m.catName)

    val btns = hl()
    val cancel = new Button
    btn(cancel, "Cancel")
    btns.addComponent(cancel)
    listenerBtn(cancel, {
      m.categoryW.close()
    })

    btn(m.catSaveBtn, "Save")
    btns.addComponent(m.catSaveBtn)

    layout.addComponent(btns)

    UI.getCurrent.addWindow(m.categoryW)
  }
}
