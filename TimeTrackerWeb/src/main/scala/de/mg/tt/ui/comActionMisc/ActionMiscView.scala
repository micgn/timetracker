package de.mg.tt.ui.comActionMisc

import com.vaadin.ui.{FormLayout, UI}
import de.mg.tt.ui.utils.LayoutUtils._

/**
  * Created by michael on 27.12.15.
  */
object ActionMiscView {

  def openActionMiscWindow(m: ActionMiscViewModel): Unit = {
    m.miscW.setCaption("Money")
    m.miscW.setModal(true)
    m.miscW.setClosable(true)
    m.miscW.setResizable(false)
    m.miscW.setDraggable(true)
    m.miscW.center()

    val layout = new FormLayout
    layout.setSpacing(true)
    layout.setMargin(true)
    m.miscW.setContent(layout)

    btn(m.exportBtn, "export")
    layout.addComponent(m.exportBtn)
    btn(m.exportPerDayBtn, "day export")
    layout.addComponent(m.exportPerDayBtn)
    btn(m.openMoneyCalc, "calculate")
    layout.addComponent(m.openMoneyCalc)
    btn(m.exportStatisticsBtn, "statistics")
    layout.addComponent(m.exportStatisticsBtn)

    UI.getCurrent.addWindow(m.miscW)
  }
}
