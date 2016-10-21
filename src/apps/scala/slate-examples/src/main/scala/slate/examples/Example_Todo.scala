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

import slate.common.{Result, FailureResult}
import slate.common.Todo._
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
import slate.common.results.ResultSupportIn
//</doc:import_examples>


class Example_Todo  extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    // About: Strongly typed, structured representation of code notes/tasks
    // This is in code to enforce consistent usage and to be able
    // to track code usages

    // Use case 1: Implement
    implement("Component 1", "This code needs further error handling" )

    // Use case 2: Supply a block of code to refactor
    refactor("Feature 2", "Refactor logic to handle empty values", Some( () => {
      // Your code to refactor goes here
    }))

    // Use case 3: Mark a bug
    bug("Component 3", "invalid data, bug fix needed", "JIRA:12434" )

    // Use case 4: Code removal tag
    remove("Story 123", "@kishore, this code no longer needed", Some( () => {
      // Your code to remove here.
    }))
    //</doc:examples>
    ok()
  }
}

