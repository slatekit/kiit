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

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec}
import slate.common.Strings
import slate.common.Strings._


class StringTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }



  describe("Basic Checks") {

    it("can get new line") {
      assert (newline() == System.lineSeparator())
    }


    it("can get value or default") {
      assert( valueOrDefault(null, "default") == "default")
      assert( valueOrDefault("", "default") == "default")
      assert( valueOrDefault("a", "default") == "a")
    }


    it("can get value option or default") {
      assert( valueOptionOrDefault(None, "default") == "default")
      assert( valueOptionOrDefault(Option(""), "default") == "default")
      assert( valueOptionOrDefault(Option("a"), "default") == "a")
    }


    it("can split") {
      assert( split(null, ',').length == 0)
      assert( split("", ',').length == 0)
      assert( split("a,b", ',').length == 2)
    }


    it("can max length") {
      assert( maxLength(List[String]("a", "bcd", "ef")) == 3)
      assert( maxLength(List[String]("a", "bcd", "efgh")) == 4)
      assert( maxLength(List[String]("a", "bcd", "ef", "12345")) == 5)
    }


    it("can pad") {
      assert( pad("abc", 3) == "abc")
      assert( pad("abc", 4) == "abc ")
      assert( pad("abc", 5) == "abc  ")
    }
  }
}
