/**
*<slate_header>
  *author: Kishore Reddy
  *url: https://github.com/kishorereddy/scala-slate
  *copyright: 2015 Kishore Reddy
  *license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  *desc: a scala micro-framework
  *usage: Please refer to license on github for more info.
*</slate_header>
  */

package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.ListMap
import slate.common.encrypt.Encryptor


class ListMapTests extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  def build():ListMap[String, Int] = {
    new ListMap[String,Int]()
  }


  test("can add") {
    val items =  new ListMap[String,Int]()
    items.add("a", 1)
    items.add("b", 2)
    items.add("c", 3)
    assert( items.size() == 3)
    assert( items.contains("a"))
    assert( items.contains("b"))
    assert( items.contains("c"))
    assert( !items.contains("d"))
  }


  test("can get by name") {
    val items =  new ListMap[String,Int]()
    items.add("a", 1)
    items.add("b", 2)
    items.add("c", 3)
    assert( items.get("a") == Some(1))
    assert( items.get("b") == Some(2))
    assert( items.get("c") == Some(3))
  }


  test("can get by index") {
    val items =  new ListMap[String,Int]()
    items.add("a", 1)
    items.add("b", 2)
    items.add("c", 3)
    assert( items.getAt(0) == Some(1))
    assert( items.getAt(1) == Some(2))
    assert( items.getAt(2) == Some(3))
  }


  test("can remove") {
    val items =  new ListMap[String,Int]()
    items.add("a", 1)
    items.add("b", 2)
    items.add("c", 3)
    assert( items.size() == 3)

    items.remove("b")
    assert( items.contains("a"))
    assert( !items.contains("b"))
    assert( items.contains("c"))
    assert( items.get("a") == Some(1))
    assert( items.get("c") == Some(3))
    assert( items.getAt(0) == Some(1))
    assert( items.getAt(1) == Some(3))
  }
}
