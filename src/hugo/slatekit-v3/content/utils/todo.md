
# Todo

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>A programmatic approach to marking and tagging code that is strongly typed and consistent</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-15</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.9</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.common.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.common</td>
    </tr>
    <tr>
      <td><strong>source core</strong></td>
      <td>slatekit.common.Todo.kt</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Todo.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Todo.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td></td>
    </tr>
  </tbody>
</table>



## Import
{{< highlight kotlin >}}


// required 
import slatekit.common.TODO



// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.Success
import slatekit.results.Try




{{< /highlight >}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}

## Usage
{{< highlight kotlin >}}


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
    

{{< /highlight >}}


