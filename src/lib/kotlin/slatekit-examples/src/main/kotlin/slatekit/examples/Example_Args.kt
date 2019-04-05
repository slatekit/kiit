/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Command
import slatekit.core.cmds.CommandRequest
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.getOrElse

//</doc:import_examples>

class Example_Args  : Command("args") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
    // Example:
    // Given on the the command line:
    // -log.level=info -env=dev -text='hello world'
    showResults( Args.parse( "-log.level=info -env=dev -text='hello world'", sep="=", hasAction = true ) )

    // CASE 1: Parse using defaults. E.g. the key/value prefix = "-", separator = ":"
    showResults( Args.parse( "-env:dev -text:'hello world' -batch:10" ) )


    // CASE 2: Custom prefix and sep e.g. "!" and separator "="
    showResults( Args.parse( "!env=dev !text='hello word' !batch=10 ", prefix = "!", sep = "=" ) )


    // CASE 3a: Check for action/method call in the beginning
    showResults( Args.parse( "area.service.method -env=dev -text='hello word' -batch=10", prefix = "-",
                 sep = "=", hasAction = true ) )


    // CASE 3b: Check for method call in the beginning
    showResults( Args.parse( "service.method -env=dev -text='hello word' -batch=10", prefix = "-",
                 sep = "=", hasAction = true ) )


    // CASE 3c: Check for only action name in the beginning.
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


    // CASE 8: Build up the schema
    val schema = ArgsSchema().text("env").flag("log").number("level")
    print(schema)

    //</doc:examples>

    return Success("")
  }


  //<doc:examples_support>
  fun showResults(result:Try<Args>)  {
    println("RESULTS:")

    if(!result.success) {
      println("Error parsing args : " + result.msg)
      return
    }
    val args = result.getOrElse { Args.default() }
    println("action   : " + args.action)
    if(!args.parts.isEmpty()) {
      print("parts    : ")
      var parts = ""
      args.parts.forEach{ item -> parts += (item + " ") }
      println( parts )
    }
    println("prefix   : '" + args.prefix + "'")
    println("separator: '" + args.separator + "'")
    println("named    : " + args.named.size )
    if(!args.named.isEmpty()) {
      args.named.forEach { pair ->
        println("\t" + pair.key + " " + args.separator + " " + pair.value)
      }
    }
    println("index    : " + args.positional.size)
    if(!args.positional.isEmpty()) { args.positional.forEach{ item -> println( "\t" + item) } }

    if(args.isHelp) println("help")
    if(args.isEmpty) println("empty")
    if(args.isVersion)println("version")
    println()
    println()
  }
  //</doc:examples_support>


  /*
  //<doc:output>
{{< highlight bat >}}
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

{{< /highlight >}}
  //</doc:output>
  */
  /**
   * Sample class to show storing of the command line options.
    *
    * @param env
   * @param text
   * @param batch
   */
  class SampleOptions(val env:String, val text:String, val batch:Int)
}
