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
import slatekit.common.TODO

//</doc:import_required>

//<doc:import_examples>
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.results.Success
import slatekit.results.Try

//</doc:import_examples>


class Example_Todo  : Command("todo") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
    // About: Strongly typed, structured representation of code notes/tasks
    // This is in code to enforce consistent usage and to be able
    // to track code usages

    // Use case 1: Implement
    TODO.IMPLEMENT("Component 1", "This code needs further error handling" )

    // Use case 2: Supply a block of code to refactor
    TODO.REFACTOR("Feature 2", "Refactor logic to handle empty values") {
      // Your code to refactor goes here
    }

    // Use case 3: Mark a bug
    TODO.BUG("Component 3", "invalid data, bug fix needed", "JIRA:12434" )

    // Use case 4: Code removal tag
    TODO.REMOVE("Story 123", "@kishore, this code no longer needed") {
      // Your code to remove here.
    }
    //</doc:examples>
    return Success("")
  }
}

