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
package de.mg.tt.model

import java.util
import de.mg.tt.util.HashHelper
import javax.persistence._
import javax.validation.constraints.NotNull
import javax.persistence.TemporalType.TIMESTAMP

import java.text.DateFormat._
import java.util.{Calendar, Date, Locale}

import scala.collection.JavaConverters._


@Entity
@Table(name = "Activity")
@NamedQuery(name = "findAllActivities", query = "select a from Activity a")
class Activity(pDescription: String, pFrom: Date, pTo: Date) extends Persistent {

  var description: String = pDescription

  @Lob
  var longDescription: String = ""

  @NotNull
  @Temporal(TIMESTAMP)
  @Column(name = "fromDate")
  var from: Date = pFrom

  @Temporal(TIMESTAMP)
  @Column(name = "toDate")
  var to: Date = pTo

  @ManyToMany(fetch = FetchType.EAGER)
  val categories: util.Set[Category] = new util.HashSet

  // make JPA happy
  def this() = this("", null, null)

  def len = ((to.getTime - from.getTime) / (1000 * 60))

  def day = cal.get(Calendar.DAY_OF_YEAR)
  def week = {val c = cal; /*c.add(Calendar.DATE, -1);*/ c.get(Calendar.WEEK_OF_YEAR)}

  private def cal = { val cal = Calendar.getInstance(); cal.setTime(from); cal }

  override def toString() = {
    val df = getDateTimeInstance(MEDIUM, MEDIUM, Locale.GERMANY)
    val fromStr = df format from
    val toStr = df format to
    s"($fromStr) to ($toStr): $description categories: $categories"
  }

  override def equals(other: Any) = other match {
    case that: Activity =>
      this.description == that.description && this.from == that.from && this.to == that.to
    case _ => false
  }

  override def hashCode() = HashHelper.hashCode(List(description, from, to))

}

class ActivityBuilder(val description: String) {

  var from: Date = null
  var to: Date = null
  var categories = scala.collection.mutable.Set[Category]()

  def from(pFrom: Date): ActivityBuilder = {
    from = pFrom; this
  }

  def to(pTo: Date): ActivityBuilder = {
    to = pTo; this
  }

  def in(pCategory: Category): ActivityBuilder = {
    categories += pCategory; this
  }

  def build: Activity = {
    // TODO validation
    val acti = new Activity(description, from, to)
    acti.categories.addAll(categories.asJava)
    return acti
  }

}

object ActivityBuilder {

  def create(description: String) = new ActivityBuilder(description)
}