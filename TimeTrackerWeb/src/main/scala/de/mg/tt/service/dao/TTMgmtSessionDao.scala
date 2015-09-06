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

import javax.annotation.{PostConstruct, PreDestroy}
import javax.ejb.TransactionAttributeType.{NEVER, REQUIRES_NEW}
import javax.ejb._
import javax.enterprise.context.SessionScoped
import javax.interceptor.Interceptors
import javax.persistence._

import de.mg.tt.model.{Activity, Category, Persistent}
import de.mg.tt.service.{FilterCriteria, ExceptionHandler}


@SessionScoped
@Stateful
@LocalBean
@TransactionAttribute(NEVER)
@Interceptors(Array(classOf[ExceptionHandler]))
class TTMgmtSessionDao {

  // since the injection of extended entity manager leads to an enetity manager,
  // which does detach entitites immediately, we create an extended entity manager manually
  //@PersistenceContext(`type` = EXTENDED)
  private var em: EntityManager = null

  @PersistenceUnit
  private val factory: EntityManagerFactory = null;

  private var delegate : TTMgmtDao = null

  @PostConstruct
  def init = {
    // create extended application-managed entity manager
    em = factory.createEntityManager()
    delegate = new TTMgmtDao(em)
  }

  @PreDestroy
  def destroy = {
    em.close()
    delegate = null
  }

  def create[T <: Persistent](entity: T)(implicit mf: Manifest[T]): T = delegate.create(entity)

  def get[T <: Persistent](id: Long)(implicit mf: Manifest[T]): T = delegate.get(id)

  def delete(entity: Persistent): Unit = delegate.delete(entity)

  def findAllCategories() = delegate.findAllCategories()

  def findActivities(criteria: FilterCriteria) = delegate.findActivities(criteria)

  @TransactionAttribute(REQUIRES_NEW)
  def save() = {
    em.joinTransaction()
  }

  def revert() = em.clear()

  def hasChanges: Boolean = {
    em.getDelegate.asInstanceOf[org.eclipse.persistence.internal.jpa.EntityManagerImpl].
      getUnitOfWork.hasChanges
  }

  @TransactionAttribute(REQUIRES_NEW)
  def cleanupDB(): Unit = {
    em.joinTransaction()
    em.createQuery("delete from Activity").executeUpdate()
    em.createQuery("delete from Category").executeUpdate()
  }

  def resetSession(): Unit = em.clear()

}