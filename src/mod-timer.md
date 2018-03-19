---
layout: start_page_mods_utils
title: module Timer
permalink: /kotlin-mod-timer
---

# Timer

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A timer to benchmark time/duration of code blocks | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common  |
| **source core** | slatekit.common.Timer.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Timer.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Timer.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.Measure



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultTimed
import slatekit.common.Random



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


    // CASE 1: Benchmark an operation 1 time
    // NOTE: Using call-by-name
    showResult(
      Measure.once("run once", {
       val ms = Random.digits3()
        Thread.sleep(ms.toLong())
        ms
      })
    )


    // CASE 2: Benchmark an operation 10 times and get all individual results
    showResults( Measure.many("run 2 times", { count ->  println( "operation : $count" ) }, 2))


    // CASE 3: Benchmark an operation 10 times and get an average
    showResult( Measure.avg("avg of 10 times", 10, { count ->

      Thread.sleep(Random.digits3().toLong())
    }))

    

```


## Output

```java
  desc           : run once
  success        : true
  message        :
  code           : 1
  data           : null
  ref            : null
  start          : 2016-07-15T17:17:54.539
  end            : 2016-07-15T17:17:55.537
  duration.secs  : 0
  duration.nanos : 998000000
  memory.used    : 0
  memory.free    : 192835456
  memory.total   : 257425408
  memory.max     : 3797417984


  desc           : run 2 times
  success        : true
  message        :
  code           : 1
  data           : null
  ref            : null
  start          : 2016-07-15T17:17:55.542
  end            : 2016-07-15T17:17:55.542
  duration.secs  : 0
  duration.nanos : 0
  memory.used    : 0
  memory.free    : 192835456
  memory.total   : 257425408
  memory.max     : 3797417984


  desc           : run 2 times
  success        : true
  message        :
  code           : 1
  data           : null
  ref            : null
  start          : 2016-07-15T17:17:55.542
  end            : 2016-07-15T17:17:55.542
  duration.secs  : 0
  duration.nanos : 0
  memory.used    : 0
  memory.free    : 192835456
  memory.total   : 257425408
  memory.max     : 3797417984


  desc           : avg of 10 times
  success        : true
  message        :
  code           : 200
  data           : null
  ref            : null
  start          : 2016-07-15T17:17:55.544
  end            : 2016-07-15T17:18:03.017
  duration.secs  : 0
  duration.nanos : 47300000
  memory.used    : 0
  memory.free    : 192835456
  memory.total   : 257425408
  memory.max     : 3797417984
```
  