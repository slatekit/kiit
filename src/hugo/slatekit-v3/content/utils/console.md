
# Console

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Enhanced printing to console with support for semantic writing like title, subtitle, url, error, etc with colors</td>
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
      <td>slatekit.common.console</td>
    </tr>
    <tr>
      <td><strong>source core</strong></td>
      <td>slatekit.common.console.Console.kt</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/console" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/console</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Console.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Console.kt</a></td>
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
import slatekit.common.console.*



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.DateTime
import slatekit.results.Try
import slatekit.results.Success



{{< /highlight >}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}

## Usage
{{< highlight kotlin >}}


    // ConsoleWriter with semantic ( title, url, error, success, highlight ) writing.
    val writer = SemanticConsole()

    // Case 1: Title - prints text in title format ( CAPS + Color Cyan )
    writer.title("title is in CAPS")

    // Case 2: Subtitle ( Color Cyan )
    writer.subTitle("subtitle is in color cyan")

    // Case 3: Url ( Color blue )
    writer.url("url is in blue")

    // Case 4: Highlight ( Color Yellow )
    writer.highlight("highlight is in color")

    // Case 5: Subtitle ( Color Red )
    writer.important("important is red")

    // Case 6: Subtitle ( Color Red )
    writer.failure("error shown in red")

    // Case 7: Subtitle ( Color Green )
    writer.success("success is in green")

    // Case 8: Tab
    writer.tab()

    // Case 9: New line
    writer.line()

    // Case 10: Label "Key :"
    writer.label("Key")

    // Case 11: Key/Value = "Name : Superman"
    writer.keyValue("Name", "Superman")
    writer.lines(2)

    // Case 12: List of items ( unordered and ordered )
    writer.list( listOf( 1, true , "www.slatekit.com", DateTime.now(), 12.34 ), true)
    writer.list( listOf( 2, false, "www.codehelix.co", DateTime.now(), 56.78 ), true)

    // Case 13: Supply a list of items to print specifying the semantic mode ( title, url, etc )
    writer.writeItems(listOf(
      SemanticOutput(SemanticText.Title     , "About App"                   , true),
      SemanticOutput(SemanticText.Subtitle  , "Example of Console component", true),
      SemanticOutput(SemanticText.Url       , "http://www.slatekit.com"     , true),
      SemanticOutput(SemanticText.Highlight , "visit us for more info"      , true)
    ))
    

{{< /highlight >}}



## Output

{{< highlight kotlin >}}
  TITLE IS IN CAPS
  subtitle is in color cyan
  url is in blue
  highlight is in color
  important is red
  error shown in red
  success is in green

  Key
  Name =  Superman


  ABOUT APP
  Example of Console component
  http://www.slatekit.com
  visit us for more info

{{< /highlight >}}
  