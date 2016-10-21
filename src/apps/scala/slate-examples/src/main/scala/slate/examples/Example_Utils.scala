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


package slate.examples

//<doc:import_required>
import slate.common.console.ConsoleWriter
import slate.common.envs._
import slate.common.{Todo, ListMap, Ensure}
import slate.common.results.{ResultSupportIn}
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
//</doc:import_examples>


class Example_Utils  extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    // Miscellaneous utilities.

    // CASE 1: ConsoleWriter with semantic ( title, url, error, success, highlight ) writing.
    val writer = new ConsoleWriter()
    writer.title("title is in CAPS")
    writer.subTitle("subtitle is in color cyan")
    writer.url("url is in blue")
    writer.highlight("highlight is in color")
    writer.important("important is red")
    writer.error("error shown in red")
    writer.success("success is in green")
    writer.line()

    // CASE 2: Environment selection
    // Build up with constructor ( key = "name"."mode" )
    val qa1 = new EnvItem("qa1", Env.QA, "dev1.env")

    // Build up with .apply
    val qa2 = EnvItem("qa1", Env.QA)
    val envs = Env.defaults().select("qa1")
    envs.isDev
    envs.isQa
    envs.isUat
    envs.isProd
    envs.current

    // CASE 3: Guards
    Ensure.isFalse( "slate-kit".length < 10, "Name must be less than 10 chars")
    Ensure.isTrue ( "slate-kit".length > 0 , "Name must be supplied" )
    Ensure.isNotNull("slate-kit", "Name must be supplied" )

    // CASE 4: ListMap
    val listMap = new ListMap[String, Int]()
    listMap("a") = 1
    listMap("b") = 2
    listMap("c") = 3

    // get by key
    println( listMap("a") )

    // get size
    println( listMap.size() )

    // get by index
    listMap.getAt(1)


    // CASE 4: Strongly typed, structured representation of Todos.
    Todo.implement("Component 1", "This code needs further error handling" )
    Todo.refactor("Component 2", "method is too long" )
    Todo.bug("Component 3", "invalid data, bug fix needed", "JIRA:12434" )
    Todo.implement("Component 4", "Store to database", Some(() =>
    {
      // This is an example of showing how to pass a closure/anonymous
      // function to the methods.
      println("inside closure of todo example")
    }))
    //</doc:examples>
    ok()
  }
}

