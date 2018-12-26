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

import java.text.DateFormat._
import java.util
import java.util.{Calendar, Date, Locale}

import de.mg.tt.util.HashHelper
import javax.persistence.TemporalType.TIMESTAMP
import javax.persistence._
import javax.validation.constraints.NotNull

import scala.collection.JavaConverters._
import scala.collection.mutable


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

  def len: Long = to.getTime./(1000 * 60) - from.getTime / (1000 * 60)

  def day: Int = cal.get(Calendar.DAY_OF_YEAR)

  def week: Int = {
    val c = cal; /*c.add(Calendar.DATE, -1);*/ c.get(Calendar.WEEK_OF_YEAR)
  }

  private def cal = { val cal = Calendar.getInstance(); cal.setTime(from); cal }

  override def toString: String = {
    val df = getDateTimeInstance(MEDIUM, MEDIUM, Locale.GERMANY)
    val fromStr = df format from
    val toStr = df format to
    s"($fromStr) to ($toStr): $description categories: $categories"
  }

  override def equals(other: Any): Boolean = other match {
    case that: Activity =>
      this.description == that.description && this.from == that.from && this.to == that.to
    case _ => false
  }

  override def hashCode(): Int = HashHelper.hashCode(List(description, from, to))

}

class ActivityBuilder(val description: String) {

  var from: Date = _
  var to: Date = _
  var categories: mutable.Set[Category] = scala.collection.mutable.Set[Category]()

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
    val acti = new Activity(description, from, to)
    acti.categories.addAll(categories.asJava)
    acti
  }

}

object ActivityBuilder {

  def create(description: String) = new ActivityBuilder(description)
}