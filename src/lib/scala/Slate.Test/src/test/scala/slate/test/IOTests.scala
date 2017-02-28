/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.test

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.common.{IoWrap, IoAction}

class IOTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }


  describe ( "Can use IO Read") {

    it("can read faked") {
      val ioRead:IoAction[Unit,String] = new IoWrap[Unit,String]((i) => "from command line")
      val result = ioRead.run("enter name:")
      assert(result.isDefined)
      assert(result.get == "from command line")
    }

    it("can read faked with err") {
      val ioRead:IoAction[Unit,String] = new IoWrap[Unit,String]((i) => throw new IllegalArgumentException("test"))
      val result = ioRead.run("enter name:")
      assert(result.isEmpty)
      assert(result.err.get.getMessage == "test")
    }
  }
}
