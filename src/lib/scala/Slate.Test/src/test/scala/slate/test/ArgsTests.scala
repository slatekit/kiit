/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.Result
import slate.common.args.{ArgsHelper, Args}

import scala.collection.mutable.ListBuffer


class ArgsTests extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {


  test("can parse args with defaults") {

    val result = Args.parse("-env:loc -log:info -region:ny")
    ensure(result, true, 3, List[(String,String)](
      ("env"   , "loc" ),
      ("log"   , "info"),
      ("region", "ny"  )
    ))
  }


  test("can parse args with custom prefix and separator") {

    val result = Args.parse("-env=loc -log=info -region=ny", "-", "=")
    ensure(result, true, 3, List[(String,String)](
      ("env"   , "loc" ),
      ("log"   , "info"),
      ("region", "ny"  )
    ))
  }


  test("can parse args single quotes") {

    val result = Args.parse("-env='loc' -log='info' -region='ny'", "-", "=")
    ensure(result, true, 3, List[(String,String)](
      ("env"   , "loc" ),
      ("log"   , "info"),
      ("region", "ny"  )
    ))
  }


  test("can parse args double quotes") {

    val result = Args.parse("-env=\"loc\" -log=\"info\" -region=\"ny\"", "-", "=")
    ensure(result, true, 3, List[(String,String)](
      ("env"   , "loc" ),
      ("log"   , "info"),
      ("region", "ny"  )
    ))
  }


  test("can parse args with dots") {

    val result = Args.parse("-env.name='loc' -log.level='info' -region.name='ny'", "-", "=")
    ensure(result, true, 3, List[(String,String)](
      ("env.name"   , "loc" ),
      ("log.level"  , "info"),
      ("region.name", "ny"  )
    ))
  }


  test("can parse actions with args") {

    val result = Args.parse("area.api.action -env.name='loc' -log.level='info' -region.name='ny'", "-", "=", true)
    ensure(result, true, 3, List[(String,String)](
      ("env.name"   , "loc" ),
      ("log.level"  , "info"),
      ("region.name", "ny"  )
    ), Some(List[String]("area", "api", "action")))
  }


  // app.users.activate
  test("can parse actions without args") {

    val result = Args.parse("area.api.action", "-", "=", true)
    ensure(result, true, 0, List[(String,String)](), Some(List[String]("area", "api", "action")))
  }

  test("is meta arg with /help") {
    assert( ArgsHelper.isMetaArg(List("help"  ), 0, "help", "info") )
    assert( ArgsHelper.isMetaArg(List("-help" ), 0, "help", "info") )
    assert( ArgsHelper.isMetaArg(List("--help"), 0, "help", "info") )
    assert( ArgsHelper.isMetaArg(List("/help" ), 0, "help", "info") )

    assert( ArgsHelper.isMetaArg(List("info"  ), 0, "help", "info") )
    assert( ArgsHelper.isMetaArg(List("-info" ), 0, "help", "info") )
    assert( ArgsHelper.isMetaArg(List("--info"), 0, "help", "info") )
    assert( ArgsHelper.isMetaArg(List("/info" ), 0, "help", "info") )

    assert( !ArgsHelper.isMetaArg(List("/about" ), 0, "help", "info"))
  }

  test("is help on area ?") {
    assert( ArgsHelper.isHelp(List[String]("app", "?"), 1) )
  }


  test("can parse args action") {
    ensureAction( ArgsHelper.parseAction(List[String](), "-"), "", 0, 0 )
    ensureAction( ArgsHelper.parseAction(List[String](""), "-"), "", 0, 1)
    ensureAction( ArgsHelper.parseAction(List[String]("activate"), "-"), "activate", 1, 1)
    ensureAction( ArgsHelper.parseAction(List[String]("app", ".", "users", ".", "activate"), "-"), "app.users.activate", 3, 5)
    ensureAction( ArgsHelper.parseAction(List[String]("app", ".", "users", ".", "activate", "?"), "-"), "app.users.activate.?", 4, 6)
    ensureAction( ArgsHelper.parseAction(List[String]("app", ".", "users", ".", "activate", "-", "a", "=", "1"), "-"), "app.users.activate", 3, 5)
  }


  private def ensureAction(result:(String, List[String], Int, Int),
                            expectedAction:String,
                            expectedVerbCount:Int,
                            expectedLastIndex:Int):Unit = {
    assert(result._1 == expectedAction)
    assert(result._3 == expectedVerbCount)
    assert(result._4 == expectedLastIndex)
  }


  private def ensure(result:Result[Args], success:Boolean, size:Int,
                     expected:List[(String,String)],
                     parts:Option[List[String]] = None) : Unit = {

    // success / fail
    assert( result.success == success )

    val args = result.get

    // size
    assert( args.size() == size)

    // expected
    for(item <- expected ){
      assert( args.containsKey(item._1))
      assert( args.get(item._1) == item._2)
    }

    if(parts.isDefined){
      assert(args.actionVerbs.size == parts.get.size)
      for(i <- 0 until parts.get.size){
        val part = parts.get(i)
        assert( args.getVerb(i) == part)
      }
    }
  }
}
