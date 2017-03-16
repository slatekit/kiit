---
layout: start_page_mods_utils
title: module Console
permalink: /mod-console
---

# Console

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Enhanced printing to console with support for semantic writing like title, subtitle, url, error, etc with colors | 
| **date**| 2017-03-12T23:33:48.921 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.console  |
| **source core** | slate.common.console.Console.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/console](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/console)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Console.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Console.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.{Result, DateTime}
import slate.common.console.ConsoleWriter


// optional 
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


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

    // Case 12: List of items ( unordered and ordered )
    writer.list( List( 1, true , "www.slatekit.com", DateTime.now, 12.34 ), true)
    writer.list( List( 2, false, "www.codehelix.co", DateTime.now, 56.78 ), true)

    // Case 13: Supply a list of items to print specifying the semantic mode ( title, url, etc )
    writer.writeItemsByText(List[(String,String,Boolean)](
      ("title"     , "About App"                   , true),
      ("subtitle"  , "Example of Console component", true),
      ("url"       , "http://www.slatekit.com"     , true),
      ("highlight" , "visit us for more info"      , true)
    ))
    

```


## Output

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
  