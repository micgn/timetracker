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

import java.io.PrintWriter
import java.util.Date
import javax.ejb._
import javax.inject.Inject
import javax.interceptor.Interceptors

import de.mg.tt.service.dao.{TTMgmtSessionDao, TTMgmtDao}
import de.mg.tt.util.DateHelper._

/**
 * Created by gnatz on 4/3/15.
 */
@Stateless
@Interceptors(Array(classOf[ExceptionHandler]))
class ExportTimer {

  private val PATH = "/home/gnatz/ttsave/"

  @Inject
  var dao: TTMgmtDao = null

  @Inject
  var exportFormatter: ExportFormatter = null

  @Schedule(minute="55", hour="23", persistent = false)
  def timeout(): Unit = {

    val criteria = new FilterCriteria(beginOfYear, dayEnd(new Date()))
    val csv = buildExportCsv(criteria)

    val fname = PATH + "ttexport-" + year() + "-" + month0Based() + "-" + dayOfMonth()

    System.out.println("saving " + csv.length + " chars for " + criteria + " to " + fname)
    val out = new PrintWriter(fname)
    out.print(csv)
    out.flush
    out.close
  }

  private def buildExportCsv(filterCriteria: FilterCriteria): String = {
    var exp = "";
    dao.findActivities(filterCriteria).foreach(a => exp += exportFormatter.toCsv(a))
    exp
  }
}
