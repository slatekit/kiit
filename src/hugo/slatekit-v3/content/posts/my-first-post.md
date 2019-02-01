---
title: "My First Post"
date: 2019-01-26T02:26:27-05:00
draft: true
type: post
layout: section
---

# Args
slatekit-site-v2 : this is the args component
customize tables resource https://discourse.gohugo.io/t/how-to-customise-tables/15661


<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>A lexical command line argument parser with optional support for allowing a route/method call in the beginning</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2018-03-19</td>
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
      <td>slatekit.common.args</td>
    </tr>
    <tr>
      <td><strong>source core</strong></td>
      <td>slatekit.common.args.Args.kt</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/" class="url-ch">src/lib/kotlin/slatekit/</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Args.kt" class="url-ch">/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Args.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td>&nbsp;</td>
    </tr>
  </tbody>
</table>


## Import
{{< highlight kotlin >}}

  // required 
  import slatekit.common.args.Args
  import slatekit.common.args.ArgsSchema
  
  
  // optional 
  import slatekit.core.cmds.Cmd
  import slatekit.common.Result
  import slatekit.common.results.ResultFuncs.ok


{{< /highlight >}}

## Setup
{{< highlight kotlin >}}

  n/a

{{< /highlight >}}

## Usage
{{< highlight kotlin >}}

    data class User(val name:String = "spiderman") {
        fun hi():String = "friendly neighborhood spiderman"
    }

    var test1 = 20L
    val test2 = 20.0
    val test3 = """
        line 1
        line 2
    """
    when(test1) {
        1 -> "a"
        2 -> "b"
        3 -> "c"
        4 -> "d"
        else -> "e"
    }
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
{{< /highlight >}}


## Output

{{< highlight bash >}}
  RESULTS:
  action   :
  prefix   : '-'
  separator: ':'
  named    : 3
    text : hello world
    batch : 10
    env : dev
  index    : 0

{{< /highlight >}}
