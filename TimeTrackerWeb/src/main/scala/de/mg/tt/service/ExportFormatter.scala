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
import java.util.{Locale, Date}
import javax.ejb.Stateless
import javax.inject.Singleton

import de.mg.tt.model.Activity
import de.mg.tt.util.DateHelper

/**
 * Created by gnatz on 4/3/15.
 */
@Stateless
class ExportFormatter {

  def toCsv(a : Activity): String = {

      var catStr = a.categories.toString
      catStr = catStr.substring(1, catStr.length - 1)

      val diff = (a.to.getTime - a.from.getTime) / (1000 * 60)

      var exp = timeStr(a.from) + ";"
      exp += timeStr(a.to) + ";"
      exp += diff + ";"
      exp += a.description + ";"
      exp += catStr + ";"
      exp += "\n"
      exp
  }

  def toCsv(d : Date, len: Long): String = {
    dateStr(d) + ";" + len + "\n";
  }

  private def timeStr(d: Date) = {
    val df = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMANY)
    df.format(DateHelper.getRightTimeDueToVaadinBug(d))
  }

  private def dateStr(d: Date) = {
    val df = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY)
    df.format(DateHelper.getRightTimeDueToVaadinBug(d))
  }
}
