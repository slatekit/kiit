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
import slate.common.args.{ArgsSchema, Args}
import slate.common.results.ResultSupportIn

//</doc:import_required>

//<doc:import_examples>
import slate.common.{Result}
import slate.core.cmds.Cmd
//</doc:import_examples>

class Example_Args extends Cmd("types")  with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    showResults( Args.parse( "-log.level=info -env=dev -text='hello world'", sep="=", hasAction = true ) )

    // CASE 1: Defaults e..g prefix = "-", sep = ":"
    showResults( Args.parse( "-env:dev -text:'hello world' -batch:10" ) )


    // CASE 2: Custom prefix and sep e.g. "!" and separator "="
    showResults( Args.parse( "!env=dev !text='hello word' !batch=10 ", prefix = "!", sep = "=" ) )


    // CASE 3a: Check for method call in the beginning
    showResults( Args.parse( "area.service.method -env=dev -text='hello word' -batch=10", prefix = "-",
                 sep = "=", hasAction = true ) )


    // CASE 3b: Check for method call in the beginning
    showResults( Args.parse( "service.method -env=dev -text='hello word' -batch=10", prefix = "-",
                 sep = "=", hasAction = true ) )


    // CASE 3c: Check for method call in the beginning
    showResults( Args.parse( "method", prefix = "-", sep = "=", hasAction = true ) )


    // CASE 4: No args
    showResults( Args.parse( "service.method", prefix = "-", sep = "=", hasAction = true ) )


    // CASE 5a: Help request ( "?", "help")
    showResults( Args.parse( "?"         ) )


    // CASE 5b: Help request with method call ( "method.action" ? )
    showResults( Args.parse( "service.method help"   , hasAction = true ) )


    // CASE 6: Version request ( "ver", "version" )
    showResults( Args.parse( "version"  ) )


    // CASE 7: Exit request
    showResults( Args.parse( "exit"     ) )


    // CASE 8: apply args to an object
    Args.apply( "-env:dev -text:'hello word' -batch:10", new SampleOptions(), "-", ":", true)

    // CASE 9: Build up the schema
    val schema = new ArgsSchema().text("env").flag("log").number("level")
    print(schema)

    //</doc:examples>

    ok()
  }


  //<doc:examples_support>
  private def showResults(result:Result[Args]):Unit =
  {
    println("RESULTS:")

    if(!result.success)
    {
      println("Error parsing args : " + result.msg)
      return
    }
    val args = result.get
    println("action   : " + args.action)
    if(!args.actionVerbs.isEmpty) {
      print("parts    : ")
      var parts = ""
      args.actionVerbs.foreach( item => parts += (item + " ") )
      println( parts )
    }
    println("prefix   : '" + args.prefix + "'")
    println("separator: '" + args.separator + "'")
    println("named    : " + args.named.size )
    if(!args.named.isEmpty) { args.named.foreach( pair => println( "\t" + pair._1 + " " + args.separator + " " + pair._2))}
    println("index    : " + args.positional.size)
    if(!args.positional.isEmpty) { args.positional.foreach( item => println( "\t" + item))}

    if(args.isHelp) println("help")
    if(args.isEmpty) println("empty")
    if(args.isVersion)println("version")
    println()
    println()
  }
  //</doc:examples_support>


  /*
  //<doc:output>
```bat
  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 3
  	text : hello world
  	batch : 10
  	env : dev
  index    : 0


  RESULTS:
  action   :
  prefix   : '!'
  separator: '='
  named    : 3
  	text = hello word
  	batch = 10
  	env = dev
  index    : 0


  RESULTS:
  action   : area.service.method
  parts    : area service method
  prefix   : '-'
  separator: '='
  named    : 3
  	text = hello word
  	batch = 10
  	env = dev
  index    : 0


  RESULTS:
  action   : service.method
  parts    : service method
  prefix   : '-'
  separator: '='
  named    : 3
  	text = hello word
  	batch = 10
  	env = dev
  index    : 0


  RESULTS:
  action   : method
  parts    : method
  prefix   : '-'
  separator: '='
  named    : 0
  index    : 0
  empty


  RESULTS:
  action   : service.method
  parts    : service method
  prefix   : '-'
  separator: '='
  named    : 0
  index    : 0
  empty


  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 0
  index    : 1
  	?
  help


  RESULTS:
  action   : service.method.help
  parts    : service method help
  prefix   : '-'
  separator: ':'
  named    : 0
  index    : 0
  empty


  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 0
  index    : 1
  	version
  version


  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 0
  index    : 1

```
  //</doc:output>
  */
  /**
   * Sample class to show storing of the command line options.
    *
    * @param env
   * @param text
   * @param batch
   */
  class SampleOptions(var env:String, var text:String, var batch:Int)
  {
    def this()
    {
      this("", "", 0)
    }
  }
}
