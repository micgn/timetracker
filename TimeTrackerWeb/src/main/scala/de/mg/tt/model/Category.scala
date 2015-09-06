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
import javax.persistence._
import de.mg.tt.util.HashHelper
import javax.validation.constraints.NotNull

@Entity
@Table(name = "Category")
@NamedQuery(name = "findAllCategories", query = "select c from Category c order by c.name asc")
class Category(pName: String) extends Persistent {

  @NotNull
  @Column(unique = true)
  var name: String = pName

  @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
  val activities: util.Set[Activity] = new util.HashSet

  // make JPA happy
  def this() = this("")

  override def toString = name

  override def equals(other: Any) = other match {
    case that: Category =>
      this.name == that.name
    case _ => false
  }

  override def hashCode() = HashHelper.hashCode(List(name))

}

object Category {
  def apply(name: String) = new Category(name)
}