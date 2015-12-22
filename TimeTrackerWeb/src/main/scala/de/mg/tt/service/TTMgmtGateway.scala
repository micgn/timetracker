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
package de.mg.tt.service

import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import javax.ejb.TransactionAttributeType._
import javax.ejb._
import javax.enterprise.context.SessionScoped
import javax.inject.Inject
import javax.interceptor.{Interceptors, Interceptor}

import de.mg.tt.model.{Activity, Category, Persistent}
import de.mg.tt.service.dao.{TTMgmtSessionDao, TTMgmtDao}
import de.mg.tt.util.DateHelper

/**
 * Created by gnatz on 1/4/15.
 */
@SessionScoped
@Stateful
@TransactionAttribute(NEVER)
@Interceptors(Array(classOf[ExceptionHandler]))
class TTMgmtGateway extends TTMgmt {

  // session variables
  private var criteria: Option[FilterCriteria] = None

  private var categoryCache: List[Category] = Nil
  private var activityCache: List[Activity] = Nil

  private var lastFilteredCats: Set[Category] = Set.empty

  @Inject
  var dao: TTMgmtSessionDao = null

  @Inject
  var exportFormatter: ExportFormatter = null

  def create[T <: Persistent](entity: T)(implicit mf: Manifest[T]): T = {
    val saved = dao.create(entity)
    saved match {
      case a: Activity => {
        activityCache ++= List(a)
        activityCache = activityCache.sortWith((a1, a2) => a1.from before a2.from)
      }
      case c: Category => {
        categoryCache ++= List(c)
        categoryCache = categoryCache.sortWith((c1, c2) => c1.name < c2.name)
      }
    }
    saved
  }

  def get[T <: Persistent](id: Long)(implicit mf: Manifest[T]): T = {
    mf.runtimeClass match {
      case a if a == classOf[Activity] => {
        val ac = activityCache.find(ac => ac.id == id)
        if (ac.isDefined) return ac.get.asInstanceOf[T]
      }
      case c if c == classOf[Category] => {
        val ca = categoryCache.find(ca => ca.id == id)
        if (ca.isDefined) return ca.get.asInstanceOf[T]
      }
    }
    return dao.get[T](id)
  }

  def delete(entity: Persistent): Unit = {
    dao.delete(entity)
    entity match {
      case a: Activity => activityCache = activityCache.diff(List(a))
      case c: Category => {
        categoryCache = categoryCache.diff(List(c))
        activityCache.foreach(a => a.categories.remove(c))
      }
    }
  }

  def deleteActivity(id: Long): Unit = delete(get[Activity](id))

  def deleteCategory(id: Long): Unit = delete(get[Category](id))

  def findAllCategories(): List[Category] =
    if (categoryCache.isEmpty) {
      val cats = dao.findAllCategories()
      categoryCache = cats
      cats
    } else categoryCache

  def findActivities(newCriteria: FilterCriteria): List[Activity] = {
    assert(newCriteria != null)
    lastFilteredCats = newCriteria.categories
    if (activityCache.isEmpty ||filterChanged(newCriteria)) {
      this.criteria = Some(newCriteria)
      val acts = dao.findActivities(newCriteria)
      activityCache = acts
      acts
    } else activityCache
  }

  def save() = {
    activityCache = Nil
    categoryCache = Nil
    dao.save()
  }

  def revert() = {
    activityCache = Nil
    categoryCache = Nil
    dao.revert()
  }

  def dataAndFilterChanged(newCriteria: FilterCriteria): Boolean = {
    assert(newCriteria != null)
    filterChanged(newCriteria) && dao.hasChanges
  }

  def hasChanges = dao.hasChanges

  private def filterChanged(newCriteria: FilterCriteria) = {
    if (this.criteria.isEmpty)
      this.criteria = Some(newCriteria)
    this.criteria.get != newCriteria
  }

  def lastFilteredCategories() = lastFilteredCats


  def buildExportCsv(filterCriteria: FilterCriteria): String = {
    var exp = "";
    findActivities(filterCriteria).foreach(a => exp += exportFormatter.toCsv(a))
    exp
  }

  def buildPerDayExportCsv(filterCriteria: FilterCriteria): String = {

    val timesPerDay =
      findActivities(filterCriteria).
      groupBy(a => DateHelper.dayStart(a.from)).
      map { case (date, activities) =>
          (date, activities.foldLeft(0L)((sum: Long, activity: Activity) => sum + activity.len)) }

    var exp = "Datum;Datum;Minuten;Stunden\n";
    timesPerDay.toList.sortBy{ case (date, len) => date }.
      foreach { case (date, len) => exp += exportFormatter.toRichCsv(date, len) };

    exp;
  }

  def resetSession(): Unit = {
    activityCache = Nil
    categoryCache = Nil
    dao.resetSession()
  }


}
