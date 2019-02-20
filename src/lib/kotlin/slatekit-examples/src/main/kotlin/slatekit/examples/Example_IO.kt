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
package slatekit.examples

//<doc:import_required>

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>

class Example_IO : Cmd("io") {

  override fun executeInternal(args: Array<String>?) : Try<Any>
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
