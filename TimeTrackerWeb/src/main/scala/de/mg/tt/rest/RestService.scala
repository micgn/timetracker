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
package de.mg.tt.rest

import java.text.{DateFormat, SimpleDateFormat}
import java.util
import java.util.Date
import javax.ejb.Stateless
import javax.inject.Inject
import javax.ws.rs._

import de.mg.tt.api._
import de.mg.tt.service.FilterCriteria
import de.mg.tt.service.dao.TTMgmtDao
import de.mg.tt.util.DateHelper._
import de.mg.tt.model._

import scala.collection.JavaConverters._


@Path("{dateStr}/")
@Stateless
class RestService {

  @Inject
  var dao: TTMgmtDao = null

  @GET
  @Produces(Array("application/xml","application/json"))
  def load(@PathParam("dateStr") dateStr: String): TTData = {

    val date = new SimpleDateFormat("ddMMyyyy").parse(dateStr)
    def criteria = new FilterCriteria(dayStart(date), dayEnd(date))
    val activities = dao.findActivities(criteria)
    val cats = dao.findAllCategories()

    val result = new TTData()
    val ttAs = new util.ArrayList[TTActivity]()
    for(a <- activities) {
      val ttA = new TTActivity()
      ttA.setFrom(a.from)
      ttA.setTo(a.to)
      ttA.setId(a.id)

      val ttCats = new util.ArrayList[String]
      a.categories.asScala.foreach(cat => ttCats.add(cat.name))
      ttA.setCategories(ttCats)

      ttAs.add(ttA);
    }
    result.setActivities(ttAs)
    result.setAvailableCategories(cats.map(cat => cat.name).asJava)
    result.setWeekMinutes(totalMinutesInWeek(date));
    result
  }

  private def totalMinutesInWeek(dayInWeek: Date): Long = {
    val criteria = new FilterCriteria(beginOfWeek(dayInWeek), endOfWeek(dayInWeek))
    val activities = dao.findActivities(criteria)
    activities.map(a => a.to.getTime() - a.from.getTime()).sum / (1000 * 60)
  }


  @POST
  @Consumes(Array("application/xml","application/json"))
  @Produces(Array("application/xml","application/json"))
  def save(@PathParam("dateStr") dateStr: String, ttData: TTData): TTData = {

    val date = new SimpleDateFormat("ddMMyyyy").parse(dateStr)

    val result = new TTData
    result.setActivities(new util.ArrayList[TTActivity]())

    def criteria = new FilterCriteria(dayStart(date), dayEnd(date))
    val persistentAs = dao.findActivities(criteria)

    val cats = dao.findAllCategories()

    // delete activities of existing any more
    val savedIds = ttData.getActivities.asScala.map(a => a.getId).filter(id => id != null)
    for (pa <- persistentAs) if (!savedIds.contains(pa.id)) dao.delete(pa)

    for (ttA <- ttData.getActivities().asScala) {
      val persistentA = persistentAs.find(a => a.id == ttA.getId)
      val pAc = if (persistentA.isDefined) {
        val pAc = persistentA.get
        pAc.from = ttA.getFrom
        pAc.to = ttA.getTo
        dao.update(pAc)
      } else {
        val ac = new Activity("", ttA.getFrom(), ttA.getTo())
        dao.create(ac)
      }

      pAc.categories.clear()
      if (ttA.getCategories != null)
        for (ttCatStr <- ttA.getCategories.asScala) {
          val pCat = cats.find(cat => cat.name == ttCatStr).get
          pAc.categories.add(pCat)
        }

      ttA.setId(pAc.id)
      result.getActivities.add(ttA)
    }

    result.setAvailableCategories(cats.map(cat => cat.name).asJava)

    result
  }

}
