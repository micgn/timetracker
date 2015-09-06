package de.mg.tt.model

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.Date


@RunWith(classOf[JUnitRunner])
class ActivityTest extends FunSuite {

  test("equals and hashcode") {
    val a1 = new Activity("test1", new Date(1000), new Date(2000))
    val a2 = new Activity("test2", new Date(3000), new Date(4000))
    assert(a1 != a2)
    assert(a1 == a1 && a2 == a2)
    assert(a1.hashCode() != a2.hashCode())
  }

  test("toString") {
    val a1 = new Activity("test1", new Date(1000), new Date(2000))
    a1.categories.add(new Category("c1"))
    a1.categories.add(new Category("c2"))
    println(a1)
  }

}
