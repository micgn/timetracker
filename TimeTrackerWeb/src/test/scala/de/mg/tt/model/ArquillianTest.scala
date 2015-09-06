package de.mg.tt.model

import javax.inject.Inject
import javax.naming.InitialContext
import javax.persistence.{PersistenceContext, EntityManager}

import de.mg.tt.service.dao.{TTMgmtSessionDao, TTMgmtDao}
import de.mg.tt.service.{TTMgmt, FilterCriteria, TTMgmtGateway}
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.junit.{Before, Test}
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import java.util.Date
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.JavaArchive
import org.jboss.arquillian.container.test.api.Deployment

@RunWith(classOf[Arquillian])
class ArquillianTest extends FunSuite {

  @Inject
  var dao: TTMgmtSessionDao = null

  @Inject
  var ttmgmt: TTMgmtGateway = null

  @PersistenceContext
  protected var em: EntityManager = null

  @Before
  def cleanup(): Unit = {
    dao.cleanupDB()
  }

  @Test
  def testCategoryCrud() {
    assert(dao != null)

    val c1 = dao.create(Category("testCategory"))
    assert(c1.id != null)
    val c2 = dao.create(Category("another"))

    assert(dao.findAllCategories().isEmpty)
    dao.save()
    val read = dao.findAllCategories()
    assert(read.length == 2 && read.contains(c1) && read.contains(c2))

    dao.delete(c1)
    dao.save()
    val read2 = dao.findAllCategories()
    assert(read2.length == 1 && read2.head == c2)
  }

  @Test
  def testActivityCrud() = {

    val c1 = dao.create(Category("c1"))
    val c2 = dao.create(Category("c2"))
    val c3 = dao.create(Category("c3"))

    val a1 = dao.create(
      ActivityBuilder.
        create("test1").
        from(new Date(100)).
        to(new Date(200)).
        in(c1).
        in(c2).
        build)

    val a2 = dao.create(
      ActivityBuilder.
        create("test2").
        from(new Date(300)).
        to(new Date(400)).
        in(c2).
        in(c3).
        build)

    dao.save()

    assert(dao.get[Category](c1.id) == c1)
    assert(dao.get[Activity](a1.id) == a1)

    val read = dao.findActivities(new FilterCriteria(new Date(10), new Date(500)))
    assert(read.length == 2 && read.contains(a1) && read.contains(a2))

    val read2 = dao.findActivities(new FilterCriteria(new Date(250), new Date(500)))
    read2.foreach(println)
    assert(read2.length == 1 && read2.head == a2)

    val read3 = dao.findActivities(new FilterCriteria(new Date(0), new Date(1)))
    assert(read3.length == 1 && read3.head == a1)

    val read4 = dao.findActivities(new FilterCriteria(new Date(0), new Date(500), Set(Category("a"), Category("b"))))
    assert(read4.length == 0)

    val read5 = dao.findActivities(new FilterCriteria(new Date(0), new Date(1), Set(c1, c2)))
    assert(read5.length == 1)

    val read6 = dao.findActivities(new FilterCriteria(new Date(0), new Date(500), Set(c2)))
    assert(read6.length == 2)
  }

  @Test
  def testUpdates() = {

    val a1 = dao.create(
      ActivityBuilder.
        create("test1").
        from(new Date(100)).
        to(new Date(200)).
        build)
    dao.save()

    a1.description = "changed"

    val read = dao.get[Activity](a1.id)
    assert(read.description == "changed")
  }

  @Test
  def testReverting() = {

    val a1 = dao.create(
      ActivityBuilder.
        create("test1").
        from(new Date(100)).
        to(new Date(200)).
        build)
    dao.save()

    a1.description = "changed"

    dao.revert()

    val read = dao.get[Activity](a1.id)
    assert(read.description == "test1")
  }

  @Test
  def testChanges = {

    val now = new Date()
    val notNow = new Date(System.currentTimeMillis()-100000)

    ttmgmt.findActivities(new FilterCriteria(now, now))

    assert(ttmgmt.dataAndFilterChanged(new FilterCriteria(now, now)) == false)
    assert(ttmgmt.dataAndFilterChanged(new FilterCriteria(notNow, notNow)) == false)

    // make session dirty
    ttmgmt.create(Category("xy"))

    assert(ttmgmt.dataAndFilterChanged(new FilterCriteria(now, now)) == false)
    assert(ttmgmt.dataAndFilterChanged(new FilterCriteria(notNow, notNow)) == true)

    ttmgmt.save()
    assert(ttmgmt.dataAndFilterChanged(new FilterCriteria(now, now)) == false)
    assert(ttmgmt.dataAndFilterChanged(new FilterCriteria(notNow, notNow)) == false)
  }

  @Test
  def testLookup() = {
    val lookedup = new InitialContext().lookup("java:module/" + classOf[TTMgmtGateway].getSimpleName).asInstanceOf[TTMgmtGateway]
    assert(lookedup != null)
  }
}

object ArquillianTest {

  @Deployment
  def createDeployment(): JavaArchive = {
    ShrinkWrap.create(classOf[JavaArchive])
      .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
      .addClasses(classOf[Activity], classOf[Category],
        classOf[TTMgmt], classOf[TTMgmtGateway],
        classOf[TTMgmtDao], classOf[FilterCriteria])
      .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
  }
}
