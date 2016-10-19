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
import slate.common.subs.{SubConstants, Sub, Subs}

class SubsTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }


  describe ( "registration") {

    it("can register subs") {
      assert ( subs().contains( "user.home" ) )
    }
  }


  describe ( "can parse") {

    it("can sub 1 only") {
      check ( subs().parse("@{user.home}"), List[(Int,String)](
        (SubConstants.TypeSub , "user.home"  )
      ))
    }


    it("can sub 1 at start") {
      check ( subs().parse("@{user.home}/slatekit/env.conf"), List[(Int,String)](
        (SubConstants.TypeSub , "user.home"  ),
        (SubConstants.TypeText, "/slatekit/env.conf")
      ))
    }


    it("can sub 1 in middle") {
      check ( subs().parse("/slatekit/@{user.home}/env.conf/"), List[(Int,String)](
        (SubConstants.TypeText, "/slatekit/"),
        (SubConstants.TypeSub , "user.home"  ),
        (SubConstants.TypeText, "/env.conf/")
      ))
    }


    it("can sub 1 at end") {
      check ( subs().parse("/slatekit/env.conf/@{user.home}"), List[(Int,String)](
        (SubConstants.TypeText, "/slatekit/env.conf/"),
        (SubConstants.TypeSub , "user.home"  )
      ))
    }


    it("can sub 2 at start") {
      check ( subs().parse("@{user.home}/@{company.id}/env.conf"), List[(Int,String)](
        (SubConstants.TypeSub  , "user.home"   ),
        (SubConstants.TypeText , "/"           ),
        (SubConstants.TypeSub  , "company.id"  ),
        (SubConstants.TypeText , "/env.conf"   )
      ))
    }


    it("can sub 2 in middle") {
      check ( subs().parse("/c/@{user.name}/@{company.id}/env.conf/"), List[(Int,String)](
        (SubConstants.TypeText , "/c/"        ),
        (SubConstants.TypeSub  , "user.name"  ),
        (SubConstants.TypeText , "/"          ),
        (SubConstants.TypeSub  , "company.id" ),
        (SubConstants.TypeText , "/env.conf/" )
      ))
    }


    it("can sub 2 at end") {
      check ( subs().parse("/c:/users/@{user.name}/@{company.id}"), List[(Int,String)](
        (SubConstants.TypeText, "/c:/users/"),
        (SubConstants.TypeSub  , "user.name"  ),
        (SubConstants.TypeText , "/"  ),
        (SubConstants.TypeSub  , "company.id" )
      ))
    }


    it("can sub 2 consecutive") {
      check ( subs().parse("@{user.name}@{company.id}"), List[(Int,String)](
        (SubConstants.TypeSub  , "user.name"  ),
        (SubConstants.TypeSub  , "company.id" )
      ))
    }
  }


  describe ( "can sub") {

    it("can substitute basic name") {
      assert ( subs()
        .resolve("@{user.home}/slatekit/env.conf") == Some("c:/users/johndoe/slatekit/env.conf") )
    }
  }


  def check(subs:List[Sub], expected:List[(Int,String)]): Unit = {
    assert( subs.size == expected.size)
    for( ndx <- 0 until expected.size ) {
      val actual = subs(ndx)
      val expect = expected(ndx)
      assert( actual.subType == expect._1.asInstanceOf[Short])
      assert( actual.text == expect._2)
    }
  }



  def subs(): Subs = {
    //<doc:setup>
    val subs = new Subs()
    subs("user.home"    ) = (s) => "c:/users/johndoe"
    subs("company.id"   ) = (s) => "slatekit"
    subs("company.dir"  ) = (s) => "@{user.home}/@{company.id}"
    subs("company.confs") = (s) => "@{user.home}/@{company.id}/confs"
    subs("app.id"       ) = (s) => "slatekit.tests"
    subs("app.dir"      ) = (s) => "@{company.dir}/@{app.id}"
    subs("app.confs"    ) = (s) => "@{app.dir}/confs"
    subs("user.name"    ) = (s) => "john.doe"
    subs
  }
}
