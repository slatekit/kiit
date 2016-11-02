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
    val items = build()
    var items2 = items + ("a", 1)
    items2 = items + ("b", 2)

    assert( items.size() == 0)
    assert( items.get("a") == None)

    assert( items2.size() == 2)
    assert( items2.get("a").get == 1)
    assert( items2.getAt(0).get == 1)
    assert( items2.get("b").get == 2)
    assert( items2.getAt(1).get == 2)
  }


  test("can add via +") {
  }


  test("can remove") {
  }


  test("can remove via -") {
  }
}
