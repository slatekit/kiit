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
import slate.common.{Random, Result}
import slate.common.templates.{TemplatePart, Template, Templates}
import slate.common.templates.TemplateConstants._

class SubsTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }


  describe ( "registration") {

    it("can register templates") {
      val templates = Templates(
        Seq(
          new Template("welcome", "Hi @{user.name}, Welcome to @{company.name}."),
          new Template("confirm", "Your confirmation code for @{app.name} is @{code}.")
        ),
        Some(List(
          ("company.name" , (s:TemplatePart) => "CodeHelix"                       ),
          ("app.name"     , (s:TemplatePart) => "slatekit.sampleapp"              ),
          ("user.name"    , (s:TemplatePart) => "john.doe"                        ),
          ("code"         , (s:TemplatePart) => Random.alpha6()                   )
        ))
      )
      assert(templates.templates.isDefined)
      assert(templates.templates.get.size == 2)
      assert(templates.templates.get.head.name == "welcome")
      assert(templates.templates.get.head.parsed)
      assert(templates.templates.get.head.valid)
      assert(templates.templates.get.head.parts.get.size == 5)
      assert(templates.subs.size == 12)
    }

    it("can register subs") {
      assert ( subs().subs.contains( "user.home" ) )
    }
  }


  describe ( "can parse") {

    it("can sub 1 only") {
      check ( subs().parse("@{user.home}"), List[(Int,String)](
        (TypeSub , "user.home"  )
      ))
    }


    it("can sub 1 at start") {
      check ( subs().parse("@{user.home}/slatekit/env.conf"), List[(Int,String)](
        (TypeSub , "user.home"  ),
        (TypeText, "/slatekit/env.conf")
      ))
    }


    it("can sub 1 in middle") {
      check ( subs().parse("/slatekit/@{user.home}/env.conf/"), List[(Int,String)](
        (TypeText, "/slatekit/"),
        (TypeSub , "user.home"  ),
        (TypeText, "/env.conf/")
      ))
    }


    it("can sub 1 at end") {
      check ( subs().parse("/slatekit/env.conf/@{user.home}"), List[(Int,String)](
        (TypeText, "/slatekit/env.conf/"),
        (TypeSub , "user.home"  )
      ))
    }


    it("can sub 2 at start") {
      check ( subs().parse("@{user.home}/@{company.id}/env.conf"), List[(Int,String)](
        (TypeSub  , "user.home"   ),
        (TypeText , "/"           ),
        (TypeSub  , "company.id"  ),
        (TypeText , "/env.conf"   )
      ))
    }


    it("can sub 2 in middle") {
      check ( subs().parse("/c/@{user.name}/@{company.id}/env.conf/"), List[(Int,String)](
        (TypeText , "/c/"        ),
        (TypeSub  , "user.name"  ),
        (TypeText , "/"          ),
        (TypeSub  , "company.id" ),
        (TypeText , "/env.conf/" )
      ))
    }


    it("can sub 2 at end") {
      check ( subs().parse("/c:/users/@{user.name}/@{company.id}"), List[(Int,String)](
        (TypeText, "/c:/users/"),
        (TypeSub  , "user.name"  ),
        (TypeText , "/"  ),
        (TypeSub  , "company.id" )
      ))
    }


    it("can sub 2 consecutive") {
      check ( subs().parse("@{user.name}@{company.id}"), List[(Int,String)](
        (TypeSub  , "user.name"  ),
        (TypeSub  , "company.id" )
      ))
    }


    it("can sub many in template") {
      check ( subs().parse(
        "hi @{user.name}, Welcome to @{company.id}, @{company.id} does abc. " +
        "visit @{company.url} for more info. regards @{company.support}"
      ), List[(Int,String)](
        (TypeText  , "hi "  ),
        (TypeSub  , "user.name"  ),
        (TypeText  , ", Welcome to "  ),
        (TypeSub  , "company.id"  ),
        (TypeText  , ", "  ),
        (TypeSub  , "company.id"  ),
        (TypeText  , " does abc. visit "  ),
        (TypeSub  , "company.url"  ),
        (TypeText  , " for more info. regards "  ),
        (TypeSub  , "company.support" )
      ))
    }
  }


  describe ( "can process") {

    val TEST_TEMPLATE = "Hi @{user.name}, Welcome to @{company.name}."


    it("can substitute basic name") {
      assert ( subs()
        .resolve("@{user.home}/slatekit/env.conf") == Some("c:/users/johndoe/slatekit/env.conf") )
    }


    it("can parse on demand template as result") {
      val result = subs().parse(TEST_TEMPLATE)
      assert( result.success )
      assert( result.get.size == 5 )
      check(result, List[(Int,String)](
        (TypeText  , "Hi "           ),
        (TypeSub   , "user.name"     ),
        (TypeText  , ", Welcome to " ),
        (TypeSub   , "company.name"  ),
        (TypeText  , "."             )
      ))
    }


    it("can parse on demand template as template") {
      val templates = new Templates()
      val result = templates.parseTemplate("welcome", TEST_TEMPLATE)
      assert( result.valid )
      assert( result.name == "welcome")
      assert( result.parsed)
      assert( result.valid )
      assert( result.content == TEST_TEMPLATE )
      assert( result.parts.get.size == 5 )

      check(result.parts.get, List[(Int,String)](
        (TypeText  , "Hi "           ),
        (TypeSub   , "user.name"     ),
        (TypeText  , ", Welcome to " ),
        (TypeSub   , "company.name"  ),
        (TypeText  , "."             )
      ))
    }


    it("can resolve on demand template with saved vars") {
      val templates = new Templates(variables = Some(
        List(
          ("company.name" , (s:TemplatePart) => "CodeHelix"                       ),
          ("app.name"     , (s:TemplatePart) => "slatekit.sampleapp"              ),
          ("user.name"    , (s:TemplatePart) => "john.doe"                        ),
          ("code"         , (s:TemplatePart) => Random.alpha6()                   )
        )
      ))
      val result = templates.resolve(TEST_TEMPLATE)
      assert(result.isDefined)
      assert(result.get == "Hi john.doe, Welcome to CodeHelix.")
    }


    it("can resolve on demand template with custom vars") {
      val templates = new Templates()
      val subs = Templates.subs(List(
        ("company.name" , (s:TemplatePart) => "CodeHelix"                       ),
        ("app.name"     , (s:TemplatePart) => "slatekit.sampleapp"              ),
        ("user.name"    , (s:TemplatePart) => "john.doe"                        ),
        ("code"         , (s:TemplatePart) => Random.alpha6()                   )
      ))

      val result = templates.resolve(TEST_TEMPLATE, Some(subs))
      assert(result.isDefined)
      assert(result.get == "Hi john.doe, Welcome to CodeHelix.")
    }


    it("can resolve saved template with saved vars") {
      val templates = Templates(
        templates = Seq(
          new Template("welcome", "Hi @{user.name}, Welcome to @{company.name}."),
          new Template("confirm", "Your confirmation code for @{app.name} is @{code}.")
        ),
        subs = Some(List(
          ("company.name" , (s:TemplatePart) => "CodeHelix"                       ),
          ("app.name"     , (s:TemplatePart) => "slatekit.sampleapp"              ),
          ("user.name"    , (s:TemplatePart) => "john.doe"                        ),
          ("code"         , (s:TemplatePart) => Random.alpha6()                   )
        ))
      )
      val result = templates.resolveTemplate("welcome", None)
      assert(result.isDefined)
      assert(result.get == "Hi john.doe, Welcome to CodeHelix.")
    }


    it("can resolve saved template with custom vars") {
      val templates = Templates(
        templates = Seq(
          new Template("welcome", "Hi @{user.name}, Welcome to @{company.name}."),
          new Template("confirm", "Your confirmation code for @{app.name} is @{code}.")
        )
      )
      val subs = Templates.subs(List(
        ("company.name" , (s:TemplatePart) => "CodeHelix"                       ),
        ("app.name"     , (s:TemplatePart) => "slatekit.sampleapp"              ),
        ("user.name"    , (s:TemplatePart) => "john.doe"                        ),
        ("code"         , (s:TemplatePart) => Random.alpha6()                   )
      ))
      val result = templates.resolveTemplate("welcome", Option(subs))
      assert(result.isDefined)
      assert(result.get == "Hi john.doe, Welcome to CodeHelix.")
    }
  }


  def check(subsResult:Result[List[TemplatePart]], expected:List[(Int,String)]): Unit = {
    assert(subsResult.success)
    val subs = subsResult.get
    check(subs, expected)
  }


  def check(subs:List[TemplatePart], expected:List[(Int,String)]): Unit = {
    assert( subs.size == expected.size)
    for( ndx <- 0 until expected.size ) {
      val actual = subs(ndx)
      val expect = expected(ndx)
      assert( actual.subType == expect._1.asInstanceOf[Short])
      assert( actual.text == expect._2)
    }
  }



  def subs(): Templates = {
    //<doc:setup>
    val templates = new Templates(
      Some(Seq(
        new Template("welcome", "Hi @{user.name}, Welcome to @{company.name}."),
        new Template("confirm", "Your confirmation code for @{app.name} is @{code}.")
      )),
      Some(List(
        ("user.home"    , (s:TemplatePart) => "c:/users/johndoe"                ),
        ("company.id"   , (s:TemplatePart) => "slatekit"                        ),
        ("company.name" , (s:TemplatePart) => "CodeHelix"                       ),
        ("company.url"  , (s:TemplatePart) => "http://www.slatekit.com"         ),
        ("company.dir"  , (s:TemplatePart) => "@{user.home}/@{company.id}"      ),
        ("company.confs", (s:TemplatePart) => "@{user.home}/@{company.id}/confs"),
        ("app.id"       , (s:TemplatePart) => "slatekit.tests"                  ),
        ("app.name"     , (s:TemplatePart) => "slatekit.sampleapp"              ),
        ("app.dir"      , (s:TemplatePart) => "@{company.dir}/@{app.id}"        ),
        ("app.confs"    , (s:TemplatePart) => "@{app.dir}/confs"                ),
        ("user.name"    , (s:TemplatePart) => "john.doe"                        ),
        ("code"         , (s:TemplatePart) => Random.alpha6()                   )
      ))
    )
    templates
  }
}
