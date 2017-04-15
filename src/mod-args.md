---
layout: start_page_mods_utils
title: module Args
permalink: /mod-args
---

# Args

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A lexical command line argument parser with optional support for specifying a schema for the arguments. | 
| **date**| 2017-04-12T22:59:11.988 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.args  |
| **source core** | slate.common.args.Args.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/args](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/args)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Args.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Args.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.args.{ArgsSchema, Args}



// optional 
import slate.common.Result
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


    import Args.parse

    showResults( parse( "-log.level=info -env=dev -text='hello world'", sep="=", hasAction = true ) )

    // CASE 1: Defaults e..g prefix = "-", sep = ":"
    showResults( parse( "-env:dev -text:'hello world' -batch:10" ) )


    // CASE 2: Custom prefix and sep e.g. "!" and separator "="
    showResults( parse( "!env=dev !text='hello word' !batch=10 ", prefix = "!", sep = "=" ) )


    // CASE 3a: Check for method call in the beginning
    showResults( parse( "area.service.method -env=dev -text='hello word' -batch=10", prefix = "-",
                 sep = "=", hasAction = true ) )


    // CASE 3b: Check for method call in the beginning
    showResults( parse( "service.method -env=dev -text='hello word' -batch=10", prefix = "-",
                 sep = "=", hasAction = true ) )


    // CASE 3c: Check for method call in the beginning
    showResults( parse( "method", prefix = "-", sep = "=", hasAction = true ) )


    // CASE 4: No args
    showResults( parse( "service.method", prefix = "-", sep = "=", hasAction = true ) )


    // CASE 5a: Help request ( "?", "help")
    showResults( parse( "?"         ) )


    // CASE 5b: Help request with method call ( "method.action" ? )
    showResults( parse( "service.method help"   , hasAction = true ) )


    // CASE 6: Version request ( "ver", "version" )
    showResults( parse( "version"  ) )


    // CASE 7: Exit request
    showResults( parse( "exit"     ) )


    // CASE 8: apply args to an object
    Args.apply( "-env:dev -text:'hello word' -batch:10", new SampleOptions(), "-", ":", true)

    // CASE 9: Build up the schema
    val schema = new ArgsSchema().text("env").flag("log").number("level")
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
  