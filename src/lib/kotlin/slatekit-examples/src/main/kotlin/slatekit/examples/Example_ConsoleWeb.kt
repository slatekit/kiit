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

//</doc:import_required>

//<doc:import_examples>
import slatekit.common.DateTime
import slatekit.common.io.Uris
import slatekit.common.console.*


import slatekit.results.Try
import slatekit.results.Success
import java.io.File

//</doc:import_examples>


class Example_ConsoleWeb : Command("console-web") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:examples>
        // ConsoleWriter with semantic ( title, url, error, success, highlight ) writing.
        val writer = WebWriter()

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
        writer.list(listOf(1, true, "www.slatekit.com", DateTime.now(), 12.34), true)
        writer.list(listOf(2, false, "www.codehelix.co", DateTime.now(), 56.78), false)

        // Case 13: Supply a list of items to print specifying the semantic mode ( title, url, etc )
        writer.writeItems(listOf(
                TextOutput(TextType.Title, "About App", true),
                TextOutput(TextType.Subtitle, "Example of Console component", true),
                TextOutput(TextType.Url, "http://www.slatekit.com", true),
                TextOutput(TextType.Highlight, "visit us for more info", true)
        ))
        val html = writer.toString()
        val path = Uris.interpret("user://slatekit-kotlin/examples/console/consoleweb.html")
        File(path!!).writeText(html)

        //</doc:examples>
        return Success("")
    }

    /*
    //<doc:output>
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
    //</doc:output>
    */
}

