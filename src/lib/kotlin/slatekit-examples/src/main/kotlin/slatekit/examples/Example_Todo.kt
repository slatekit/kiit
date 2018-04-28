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


package slatekit.examples

//<doc:import_required>
import TODO.BUG
import TODO.IMPLEMENT
import TODO.REFACTOR
import TODO.REMOVE
import slatekit.common.DateTime
import slatekit.common.console.*

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
//</doc:import_examples>


class Example_Todo  : Cmd("todo") {

  override fun executeInternal(args: Array<String>?) : ResultEx<Any>
  {
    //<doc:examples>
    // About: Strongly typed, structured representation of code notes/tasks
    // This is in code to enforce consistent usage and to be able
    // to track code usages

    // Use case 1: Implement
    IMPLEMENT("Component 1", "This code needs further error handling" )

    // Use case 2: Supply a block of code to refactor
    REFACTOR("Feature 2", "Refactor logic to handle empty values", {
      // Your code to refactor goes here
    })

    // Use case 3: Mark a bug
    BUG("Component 3", "invalid data, bug fix needed", "JIRA:12434" )

    // Use case 4: Code removal tag
    REMOVE("Story 123", "@kishore, this code no longer needed", {
      // Your code to remove here.
    })
    //</doc:examples>
    return Success("")
  }
}

