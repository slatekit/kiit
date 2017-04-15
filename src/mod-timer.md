---
layout: start_page_mods_utils
title: module Timer
permalink: /mod-timer
---

# Timer

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A timer to benchmark time/duration of code blocks | 
| **date**| 2017-04-12T22:59:15.258 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common  |
| **source core** | slate.common.Timer.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/common](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/common)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Timer.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Timer.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.results.{ResultSupportIn, ResultTimed}
import slate.common.{Result, Random, Timer}



// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


    // CASE 1: Benchmark an operation 1 time
    // NOTE: Using call-by-name
    showResult(
      Timer.once[Int]("run once", {
       val ms = Random.digits3()
        Thread.sleep(ms)
        ms
      })
    )


    // CASE 2: Benchmark an operation 10 times and get all individual results
    showResults( Timer.many("run 2 times", (count) => { println( s"operation : $count" ) }, 2))


    // CASE 3: Benchmark an operation 10 times and get an average
    showResult( Timer.avg("avg of 10 times", 10, (count) =>
    {
      Thread.sleep(Random.digits3())
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
  