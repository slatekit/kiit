/**
  * <slate_header>
  * author: Kishore Reddy
  * url: www.github.com/code-helix/slatekit
  * copyright: 2016 Kishore Reddy
  * license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  * desc: A tool-kit, utility library and server-backend
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slatekit.examples

//<doc:import_required>

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Command
import slatekit.core.cmds.CommandRequest
import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>

class Example_IO : Command("io") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
/*
    PrintLn("hello").map( item => {

      println("done")

    })
*/
    //</doc:examples>
    return Success("")
  }
}
