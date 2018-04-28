/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.examples

//<doc:import_required>
import slatekit.common.*

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.results.ResultTimed

//</doc:import_examples>

/**
  * Created by kreddy on 3/21/2016.
  */
class Example_Timer : Cmd("timer") {

  override fun executeInternal(args: Array<String>?) : ResultEx<Any>
  {
    //<doc:examples>
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

    //</doc:examples>
    return Success("")
  }


  fun showResult(result: ResultTimed<Any>):Unit
  {
    result.print()
    println()
    println()
  }


  fun showResults(results: List<ResultTimed<Any>>):Unit
  {
    for(result in results){
      showResult(result)
    }
  }

/*
  //<doc:output>
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
  //</doc:output>
  */
}
