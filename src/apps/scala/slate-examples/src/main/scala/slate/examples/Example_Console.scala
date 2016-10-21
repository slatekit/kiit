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
import slate.common.console.ConsoleWriter
import slate.common.results.ResultSupportIn
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
//</doc:import_examples>


class Example_Console  extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    // ConsoleWriter with semantic ( title, url, error, success, highlight ) writing.
    val writer = new ConsoleWriter()

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
    writer.error("error shown in red")

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

    // Case 12: Supply a list of items to print specifying the semantic mode ( title, url, etc )
    writer.writeItems(List[(String,String,Boolean)](
      ("title"     , "About App", true),
      ("subtitle"  , "Example of Console component", true),
      ("url"       , "http://www.slatekit.com", true),
      ("highlight" , "visit us for more info", true)
    ))
    //</doc:examples>
    ok()
  }

  /*
  //<doc:output>
```java
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

```
  //</doc:output>
  */
}

