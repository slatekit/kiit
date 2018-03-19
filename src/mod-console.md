---
layout: start_page_mods_utils
title: module Console
permalink: /kotlin-mod-console
---

# Console

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Enhanced printing to console with support for semantic writing like title, subtitle, url, error, etc with colors | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.console  |
| **source core** | slatekit.common.console.Console.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Console.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Console.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.console.*



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


    // ConsoleWriter with semantic ( title, url, error, success, highlight ) writing.
    val writer = ConsoleWriter()

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
    writer.list( listOf( 1, true , "www.slatekit.com", DateTime.now(), 12.34 ), true)
    writer.list( listOf( 2, false, "www.codehelix.co", DateTime.now(), 56.78 ), true)

    // Case 13: Supply a list of items to print specifying the semantic mode ( title, url, etc )
    writer.writeItemsByText(listOf(
      ConsoleItem(Title     , "About App"                   , true),
      ConsoleItem(Subtitle  , "Example of Console component", true),
      ConsoleItem(Url       , "http://www.slatekit.com"     , true),
      ConsoleItem(Highlight , "visit us for more info"      , true)
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
  