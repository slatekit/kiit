---
layout: start_page_mods_utils
title: module Args
permalink: /kotlin-mod-args
---

# Args

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A lexical command line argument parser with optional support for allowing a route/method call in the beginning | 
| **date**| 2018-02-10 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.args  |
| **source core** | slatekit.common.args.Args.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Args.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Args.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema


// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


    // Example:
    // Given on the the command line:
    // -log.level=info -env=dev -text='hello world'
    showResults( Args.parse( "-log.level=info -env=dev -text='hello world'", sep="=", hasAction = true ) )

    // CASE 1: Parse using defaults. E.g. the key/value prefix = "-", separator = ":"
    showResults( Args.parse( "-env:dev -text:'hello world' -batch:10" ) )


    // CASE 2: Custom prefix and sep e.g. "!" and separator "="
    showResults( Args.parse( "!env=dev !text='hello word' !batch=10 ", prefix = "!", sep = "=" ) )


    // CASE 3a: Check for action/method call in the beginning
    showResults( Args.parse( "area.service.method -env=dev -text='hello word' -batch=10", prefix = "-",
                 sep = "=", hasAction = true ) )


    // CASE 3b: Check for method call in the beginning
    showResults( Args.parse( "service.method -env=dev -text='hello word' -batch=10", prefix = "-",
                 sep = "=", hasAction = true ) )


    // CASE 3c: Check for only action name in the beginning.
    showResults( Args.parse( "method", prefix = "-", sep = "=", hasAction = true ) )


    // CASE 4: No args
    showResults( Args.parse( "service.method", prefix = "-", sep = "=", hasAction = true ) )


    // CASE 5a: Help request ( "?", "help")
    showResults( Args.parse( "?"         ) )


    // CASE 5b: Help request with method call ( "method.action" ? )
    showResults( Args.parse( "service.method help"   , hasAction = true ) )


    // CASE 6: Version request ( "ver", "version" )
    showResults( Args.parse( "version"  ) )


    // CASE 7: Exit request
    showResults( Args.parse( "exit"     ) )


    // CASE 8: Build up the schema
    val schema = ArgsSchema().text("env").flag("log").number("level")
    print(schema)

    

```


## Output

```bat
  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 3
  	text : hello world
  	batch : 10
  	env : dev
  index    : 0


  RESULTS:
  action   :
  prefix   : '!'
  separator: '='
  named    : 3
  	text = hello word
  	batch = 10
  	env = dev
  index    : 0


  RESULTS:
  action   : area.service.method
  parts    : area service method
  prefix   : '-'
  separator: '='
  named    : 3
  	text = hello word
  	batch = 10
  	env = dev
  index    : 0


  RESULTS:
  action   : service.method
  parts    : service method
  prefix   : '-'
  separator: '='
  named    : 3
  	text = hello word
  	batch = 10
  	env = dev
  index    : 0


  RESULTS:
  action   : method
  parts    : method
  prefix   : '-'
  separator: '='
  named    : 0
  index    : 0
  empty


  RESULTS:
  action   : service.method
  parts    : service method
  prefix   : '-'
  separator: '='
  named    : 0
  index    : 0
  empty


  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 0
  index    : 1
  	?
  help


  RESULTS:
  action   : service.method.help
  parts    : service method help
  prefix   : '-'
  separator: ':'
  named    : 0
  index    : 0
  empty


  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 0
  index    : 1
  	version
  version


  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 0
  index    : 1

```
  