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
package de.mg.tt.service.dao

import javax.ejb._
import javax.interceptor.Interceptors
import javax.persistence._

import de.mg.tt.model.{Activity, Category, Persistent}
import de.mg.tt.service.{ExceptionHandler, FilterCriteria}

import scala.collection.JavaConverters._


@Stateless
@LocalBean
@Interceptors(Array(classOf[ExceptionHandler]))
class TTMgmtDao {

  @PersistenceContext
  private var em: EntityManager = null

  def this(em: EntityManager) = {
    this()
    this.em = em
  }


  def create[T <: Persistent](entity: T)(implicit mf: Manifest[T]): T = {
    em.persist(entity)
    entity
  }

  def update[T <: Persistent](entity: T)(implicit mf: Manifest[T]): T = {
    em.merge(entity)
    entity
  }

  def get[T <: Persistent](id: Long)(implicit mf: Manifest[T]): T =
    em.getReference(mf.runtimeClass, id).asInstanceOf[T]

  def delete(entity: Persistent): Unit = {
    entity match {
      case c: Category => c.activities.remove(c)
      case a: Activity => ;
    }
    em.remove(entity)
  }

  def findAllCategories() =
    resultList[Category](em.createNamedQuery("findAllCategories"))

  def findActivities(criteria: FilterCriteria) = {
    assert(criteria != null)
    var q = "select a from Activity a where a.from >= :fromDate and a.to <= :toDate"
    var i = 0
    criteria.categories.foreach(
      elem => {
        q += s""" and exists (select c${i} from Category c${i} where a.categories = c${i} and c${i}.name = "${elem.name}")""";
        i += 1
      })
    q += " order by a.from asc"

    resultList[Activity](em.createQuery(q)
      .setParameter("fromDate", criteria.from)
      .setParameter("toDate", criteria.to)
      .setMaxResults(5000))
  }

  def findAllActivities() = resultList[Activity](em.createNamedQuery("findAllActivities"))

  private def resultList[T](query: Query) = query.getResultList.asScala.toList.asInstanceOf[List[T]]

}