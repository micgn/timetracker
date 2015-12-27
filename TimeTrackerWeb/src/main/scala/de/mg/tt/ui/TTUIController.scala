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

/**
 * Created by gnatz on 12/27/14.
 */

import java.io.{ByteArrayInputStream, InputStream}
import java.text.SimpleDateFormat
import java.util
import java.util.{Date, Locale}
import javax.inject.Inject

import com.vaadin.annotations.{Theme, Widgetset}
import com.vaadin.cdi.CDIUI
import com.vaadin.server.StreamResource.StreamSource
import com.vaadin.server.{FileDownloader, StreamResource, VaadinRequest}
import com.vaadin.ui._
import de.mg.tt.model.{Activity, Category}
import de.mg.tt.service.{FilterCriteria, TTMgmtGateway}
import de.mg.tt.ui.comActionMisc.{ActionMiscView, ActionMiscViewModel}
import de.mg.tt.ui.controller.{MoneyController, DaysController}
import de.mg.tt.ui.utils.ListenerUtils
import ListenerUtils._
import de.mg.tt.ui.compAction.ActionViewModel
import de.mg.tt.ui.compActivity.{ActivityView, ActivityViewModel}
import de.mg.tt.ui.compCategory.{CategoryView, CategoryViewModel}
import de.mg.tt.ui.compFilter.FilterViewModel
import de.mg.tt.ui.compMoney.MoneyCalcViewModel
import de.mg.tt.util.DateHelper._
import org.joda.time.LocalDate

import scala.collection.JavaConverters._

@CDIUI("")
@Widgetset("TTWidgetset")
@Theme("tttheme")
class TTUIController extends UI {

  @Inject
  private var service: TTMgmtGateway = null

  //val service: TTMgmt = TTMgmtMock.instance

  val filterVM = new FilterViewModel
  val actionVM = new ActionViewModel
  val actionMiscVM = new ActionMiscViewModel
  val activityVM = new ActivityViewModel
  val categoryVM = new CategoryViewModel
  val moneyVM = new MoneyCalcViewModel

  val table = new Table
  var activity4Update: Option[Activity] = None
  var filterCriteria: FilterCriteria = null

  override def init(r: VaadinRequest): Unit = {
    val layout = new TTLayout(this, filterVM, actionVM)
    layout.setupLayout
    initData
    registerListeners
    MoneyController.registerListeners(actionMiscVM.openMoneyCalc, moneyVM.moneyCalcBtn, moneyVM, this, service)
    val daysController = new DaysController(filterVM)
    daysController.init
  }

  def initData = {
    filterVM.filterFrom.setLocalDate(LocalDate.fromDateFields(mondayOfWeek))
    filterVM.filterTo.setValue(endOfMonth())

    reloadCategories
    val cats = service.lastFilteredCategories()
    cats.foreach(c => filterVM.filterCat.select(c.id))
    filterCriteria = new FilterCriteria(mondayOfWeek, endOfMonth(), cats)

    table.addContainerProperty("select", classOf[CheckBox], false)
    table.addContainerProperty("w", classOf[String], false)
    table.addContainerProperty("date", classOf[Label], null)
    table.addContainerProperty("from", classOf[String], null)
    table.addContainerProperty("to", classOf[String], null)
    table.addContainerProperty("length", classOf[String], null)
    table.addContainerProperty("description", classOf[String], null)
    table.addContainerProperty("categories", classOf[String], null)
    table.addContainerProperty("day", classOf[String], null)
    table.addContainerProperty("week", classOf[String], null)
    table.addContainerProperty("month", classOf[String], null)
    table.addContainerProperty("year", classOf[String], null)

    reloadTable
  }

  def registerListeners = {

    globalListenerMethods = List(() => saveRevertEnabler)
    saveRevertEnabler

    def saveRevertEnabler = {
      val changes = service.hasChanges
      actionVM.saveBtn.setEnabled(changes)
      actionVM.revertBtn.setEnabled(changes)
    }

    listener(actionVM.newBtn, {
      activity4Update = None
      activityVM.actFrom.setValue(new Date)
      activityVM.actTo.setValue(new Date)
      activityVM.actDescr.setValue("")
      activityVM.actLongDescr.setValue("")
      initWindowCategories
      activityVM.actCats.setValue(null)
      selectedCategories(filterVM.filterCat).foreach(c => activityVM.actCats.select(c.id))
      ActivityView.openActivityWindow(activityVM)
    })

    // edit on table selection
    tableListener(table, aId => {
      val act = service.get[Activity](aId)
      activity4Update = Some(act)
      activityVM.actFrom.setValue(act.from)
      activityVM.actTo.setValue(act.to)
      activityVM.actDescr.setValue(act.description)
      activityVM.actLongDescr.setValue(if (act.longDescription != null) act.longDescription else "")
      initWindowCategories
      activityVM.actCats.setValue(act.categories.asScala.map(c => c.id).seq.asJava)
      ActivityView.openActivityWindow(activityVM)
    })

    def initWindowCategories = {
      service.findAllCategories().foreach(c => {
        activityVM.actCats.addItem(c.id)
        activityVM.actCats.setItemCaption(c.id, c.name)
      })
    }

    listener(activityVM.actAddBtn, {
      if (activityVM.actFrom.getValue == null || activityVM.actTo.getValue == null ||
        activityVM.actFrom.getValue.after(activityVM.actTo.getValue)) {
        Notification.show("Activity invalid", Notification.Type.WARNING_MESSAGE)
      } else {
        val a = activity4Update.getOrElse(new Activity)
        a.from = activityVM.actFrom.getValue
        a.to = activityVM.actTo.getValue
        a.description = activityVM.actDescr.getValue
        a.longDescription = activityVM.actLongDescr.getValue
        a.categories.clear()
        selectedCategories(activityVM.actCats).foreach(c => a.categories.add(c))
        if (activity4Update.isEmpty) service.create(a)
        activityVM.activityW.close()
        Notification.show("Activity saved", Notification.Type.TRAY_NOTIFICATION)
        reloadTable
      }
    })

    listener(activityVM.actAddSaveBtn, {
      activityVM.actAddBtn.click(); actionVM.saveBtn.click()
    })

    listener(actionVM.newCatBtn, {
      categoryVM.catName.setValue("")
      CategoryView.openCategoryWindow(categoryVM)
    })

    listener(categoryVM.catSaveBtn,
      if (categoryVM.catName.getValue.length > 0) {
        service.create(new Category(categoryVM.catName.getValue))
        categoryVM.categoryW.close()
        reloadCategories
        Notification.show("Category saved", Notification.Type.TRAY_NOTIFICATION)
      })

    listener(actionVM.deleteBtn, needsSelection({
      selectedActivities.foreach(aId => service.deleteActivity(aId))
      reloadTable
      Notification.show("deleted", Notification.Type.TRAY_NOTIFICATION)
    }))

    listener(actionVM.addCatBtn, needsSelection({
      selectedActivities.foreach(aId => {
        val act = service.get[Activity](aId)
        val cId = actionVM.catChooser.getValue.asInstanceOf[Long]
        val cat = service.get[Category](cId)
        act.categories.add(cat)
        reloadTable
      })
      actionVM.catChooser.setValue(null)
      actionVM.addCatBtn.setEnabled(false)
      Notification.show("added", Notification.Type.TRAY_NOTIFICATION)
    }))

    listener(actionVM.delCatBtn, {
      val cId = actionVM.delCatChooser.getValue.asInstanceOf[Long]
      service.deleteCategory(cId)
      reloadCategories
      reloadTable
      Notification.show("Category deleted", Notification.Type.TRAY_NOTIFICATION)
    })

    def needsSelection(toExecute: => Unit) =
      if (selectedActivities.isEmpty)
        Notification.show("nothing selected", Notification.Type.WARNING_MESSAGE)
      else toExecute

    listener(actionVM.saveBtn, {
      service.save()
      Notification.show("saved", Notification.Type.TRAY_NOTIFICATION)
    })

    listener(actionVM.revertBtn, {
      TTLayout.question("really revert?", {
        service.revert()
        initData
        Notification.show("reverted", Notification.Type.TRAY_NOTIFICATION)
      })
    })

    dateFieldListener(filterVM.filterFrom, dFrom => {
      if (dFrom.after(filterVM.filterTo.getValue)) filterVM.filterTo.setValue(endOfMonth(dFrom))
    })

    listener(filterVM.filterBtn, {
      filterCriteria = new FilterCriteria(
        dayStart(filterVM.filterFrom.getLocalDate.toDate),
        dayEnd(filterVM.filterTo.getValue),
        selectedCategories(filterVM.filterCat).toSet)
      reloadTable
    })

    listener(filterVM.resetSessionBtn, {
      if (service.dataAndFilterChanged(filterCriteria))
        TTLayout.question("Revert all changes?", {
          service.resetSession; reloadTable
        })
      else {
        service.resetSession; reloadTable
      }
    })

    // table item selection
    listener(actionVM.allBtn, selectFunc(cb => cb.setValue(true)))
    listener(actionVM.noneBtn, selectFunc(cb => cb.setValue(false)))
    listener(actionVM.invertBtn, selectFunc(cb => cb.setValue(!cb.getValue)))
    def selectFunc(change: CheckBox => Unit) = {
      table.getItemIds.toArray.foreach(
        aId => {
          val cb = table.getItem(aId).getItemProperty("select").getValue.asInstanceOf[CheckBox]
          change(cb)
        }
      )
    }

    // TODO create separate MiscController

    // register downloader at export button
    new FileDownloader(exportStream).extend(actionMiscVM.exportBtn)

    new FileDownloader(exportPerDayStream).extend(actionMiscVM.exportPerDayBtn)

    new FileDownloader(exportStatisticsStream).extend(actionMiscVM.exportStatisticsBtn)

    listener(actionVM.openMiscBtn, ActionMiscView.openActionMiscWindow(actionMiscVM))
  }

  def reloadCategories = {
    filterVM.filterCat.removeAllItems()
    actionVM.catChooser.removeAllItems()
    actionVM.delCatChooser.removeAllItems()

    service.findAllCategories().foreach(c => {
      filterVM.filterCat.addItem(c.id)
      filterVM.filterCat.setItemCaption(c.id, c.name)
      actionVM.catChooser.addItem(c.id)
      actionVM.catChooser.setItemCaption(c.id, c.name)
      actionVM.delCatChooser.addItem(c.id)
      actionVM.delCatChooser.setItemCaption(c.id, c.name)
    })
  }

  def reloadTable = {

    if (service.dataAndFilterChanged(filterCriteria))
      TTLayout.question("Revert all changes?", doReload)
    else
      doReload

    def calcSums(activities: List[Activity], sum: Long, typeOfSum: Date => Int): List[Option[Long]] = {
      activities match {
        case a1 :: a2 :: tail => {
          if (typeOfSum(a1.from) != typeOfSum(a2.from))
            Some(sum + a1.len) :: calcSums(a2 :: tail, 0, typeOfSum)
          else
            None :: calcSums(a2 :: tail, sum + a1.len, typeOfSum)
        }
        case a1 :: Nil => {
          Some(sum + a1.len) :: Nil
        }
        case Nil => Nil
      }
    }

    def doReload = {
      val df = new SimpleDateFormat("EE dd.MM.yy", Locale.GERMANY)
      val tf = new SimpleDateFormat("HH:mm", Locale.GERMANY)
      def toTime(d: Date) = tf.format(getRightTimeDueToVaadinBug(d))

      table.removeAllItems

      val activities = service.findActivities(filterCriteria)

      var daySums = calcSums(activities, 0, dayOfMonth)
      var weekSums = calcSums(activities, 0, weekOfYear)
      var monthSums = calcSums(activities, 0, month0Based)
      var yearSums = calcSums(activities, 0, year)

      var prevWeek = -1;
      var prevDay = -1
      activities.foreach(a => {
        var catStr = a.categories.toString
        catStr = catStr.substring(1, catStr.length - 1)
        val cb = new CheckBox

        val diff = (a.to.getTime - a.from.getTime) / (1000 * 60)

        val daySum = daySums.head;
        daySums = daySums.tail
        val weekSum = weekSums.head;
        weekSums = weekSums.tail
        val monthSum = monthSums.head;
        monthSums = monthSums.tail
        val yearSum = yearSums.head;
        yearSums = yearSums.tail

        val newDay = prevDay != a.day
        prevDay = a.day
        val newWeek = prevWeek != a.week
        prevWeek = a.week

        val weekInYear = if (newWeek) a.week.toString else "";

        table.addItem(Array[Object](
          cb,
          weekInYear,
          TTLayout.tableDayLabel(df.format(a.from), newDay = newDay, newWeek = newWeek),
          toTime(a.from),
          toTime(a.to),
          format(diff),
          a.description,
          catStr,
          formatOpt(daySum), formatOpt(weekSum), formatOpt(monthSum), formatOpt(yearSum)
        ),
          a.id)
      })
    }

  }


  def exportStream: StreamResource = {
    new StreamResource(new StreamSource {
      override def getStream: InputStream = {
        new ByteArrayInputStream(export.getBytes("UTF-8"))
      }
    }, "ttexport.csv")
  }

  def export: String = {
    reloadTable
    service.buildExportCsv(filterCriteria)
  }

  def exportPerDayStream: StreamResource = {
    new StreamResource(new StreamSource {
      override def getStream: InputStream = {
        new ByteArrayInputStream(exportPerDay.getBytes("UTF-8"))
      }
    }, "days.csv")
  }

  def exportPerDay: String = {
    reloadTable
    service.buildPerDayExportCsv(filterCriteria)
  }

  def exportStatisticsStream: StreamResource = {
    new StreamResource(new StreamSource {
      override def getStream: InputStream = {
        new ByteArrayInputStream(service.buildStatisticsCsv.getBytes("UTF-8"))
      }
    }, "statistics.csv")
  }

  def selectedCategories(tcs: TwinColSelect): Iterable[Category] =
    tcs.getValue.asInstanceOf[util.Collection[Long]].asScala.map(
      cId => service.get[Category](cId))

  def selectedActivities: Iterable[Long] =
    table.getItemIds.asScala.filter(
      aId => table.getItem(aId).getItemProperty("select").getValue.asInstanceOf[CheckBox].getValue == true).
      asInstanceOf[Iterable[Long]]

}