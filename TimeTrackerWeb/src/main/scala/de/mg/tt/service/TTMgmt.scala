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

import javax.ejb.{LocalBean, Stateful}
import javax.enterprise.context.SessionScoped
import javax.inject.Inject

import de.mg.tt.model.{Activity, Category, Persistent}

/**
  * Created by gnatz on 1/4/15.
  */
abstract class TTMgmt {

   def create[T <: Persistent](entity: T)(implicit mf: Manifest[T]): T

   def get[T <: Persistent](id: Long)(implicit mf: Manifest[T]): T

   def delete(entity: Persistent): Unit

   def deleteActivity(id: Long): Unit

   def deleteCategory(id: Long): Unit

   def findAllCategories(): List[Category]

   def findActivities(criteria: FilterCriteria): List[Activity]

   def save(): Unit

   def revert(): Unit

   def dataAndFilterChanged(criteria: FilterCriteria): Boolean

   def hasChanges: Boolean

   def lastFilteredCategories(): Set[Category]

   def buildExportCsv(filterCriteria: FilterCriteria): String

   def buildPerDayExportCsv(filterCriteria: FilterCriteria): String

   def resetSession(): Unit

 }
