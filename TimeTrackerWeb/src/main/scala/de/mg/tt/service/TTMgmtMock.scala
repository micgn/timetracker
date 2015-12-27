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

import java.util.Date

import de.mg.tt.model.{Activity, ActivityBuilder, Category, Persistent}


/**
 * Created by gnatz on 12/28/14.
 */
class TTMgmtMock extends TTMgmt {

  private var id = 100
  private var cats: List[Category] = Nil
  private var activities: List[Activity] = Nil

  private var changes = false
  private var criteria: Option[FilterCriteria] = None

  def create[T <: Persistent](entity: T)(implicit mf: Manifest[T]): T = {
    changes = true
    entity.id = id;
    id += 1
    entity match {
      case a: Activity => activities ++= List(a)
      case c: Category => cats ++= List(c)
    }
    entity
  }

  def delete(entity: Persistent): Unit = {
    changes = true
    entity match {
      case a: Activity => activities = activities.diff(List(a))
      case c: Category => cats = cats.diff(List(c))
    }
  }

  def deleteActivity(id: Long): Unit = {
    changes = true
    delete(activities.find(a => a.id == id).get)
  }

  def deleteCategory(id: Long): Unit = {
    changes = true
    delete(cats.find(c => c.id == id).get)
  }


  def findAllCategories() = {
    if (cats == Nil) {
      val c1 = new Category("c 1")
      c1.id = 1
      val c2 = new Category("c 2")
      c2.id = 2
      cats = List[Category](c1, c2)
    }
    cats
  }

  def findActivities(criteria: FilterCriteria) = {

    this.criteria = Some(criteria)

    if (activities == Nil) {

      val c1 = new Category("c 1")
      c1.id = 1
      val c2 = new Category("c 2")
      c2.id = 2

      val now = System.currentTimeMillis()

      val a1 = ActivityBuilder.
        create("test 1").
        from(new Date(now - 1000 * 60 * 60 * 3)).
        to(new Date(now)).
        in(c1).
        in(c2).
        build
      a1.id = 1

      val a2 = ActivityBuilder.
        create("test 2").
        from(new Date(now - 1000 * 60 * 60 * 2)).
        to(new Date(now)).
        in(c1).
        in(c2).
        build
      a2.id = 2

      activities = List(a1, a2)
    }
    activities
  }

  def get[T <: Persistent](id: Long)(implicit t: Manifest[T]): T = t.runtimeClass match {
    case t if t == classOf[Activity] => activities.find(a => a.id == id).get.asInstanceOf[T]
    case t if t == classOf[Category] => cats.find(c => c.id == id).get.asInstanceOf[T]
  }

  def revert() = {
    changes = false
    cats = Nil
    activities = Nil
  }

  def save() = changes = false

  def dataAndFilterChanged(criteria: FilterCriteria): Boolean =
    changes && this.criteria.forall(c => c != criteria)

  def lastFilteredCategories() = Set.empty

  def hasChanges = true

  def buildExportCsv(filterCriteria: FilterCriteria): String = ""

  def buildPerDayExportCsv(filterCriteria: FilterCriteria): String = ""

  def resetSession(): Unit = {}

  def buildStatisticsCsv: String = ""
}

object TTMgmtMock {
  lazy val instance = new TTMgmtMock
}
