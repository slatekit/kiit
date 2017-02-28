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

package slate.common


import java.time.Duration
import java.time.temporal.{ChronoUnit}
import java.util.concurrent.TimeUnit

import slate.common.DateTime._
import slate.common.info.MemUsage
import slate.common.results.{ResultSupportIn, ResultTimed}


object Timer extends ResultSupportIn
{

  /**
    * benchmarks an operation once
    *
    * @param desc     : description of the operation
    * @param callback : the callback to execute
    * @return
    */
  def once[T](desc:String, callback: => T): ResultTimed[T] = {

    val res = wrap[T](desc, 1, () => Funcs.attempt[T]( () => callback ))
    res
  }


  /**
    * benchmarks an operation several times and returns a list of benchmarked results
 *
    * @param desc     : description of the operation
    * @param callback : the callback to execute
    * @param count    : the number of times to run
    * @return
    */
  def many[T](desc:String, callback:(Int) => T, count:Int): List[ResultTimed[T]] = {

    val results = for {
      ndx <- 1.until(count)
    } yield  wrap[T](desc, ndx, () => Funcs.attempt[T]( () => callback(ndx) ))

    results.toList
  }


  /**
   * benchmarks an operation several times and returns an average duration for each run
   *
   * @param desc     : description of the operation
   * @param callback : the callback to execute
   * @param count    : the number of times to run
   * @return
   */
  def avg[T](desc:String, count:Int, callback:(Int) => T): ResultTimed[T] = {

    // 0 to X
    val indexes = 1.until(count).indices

    // Repeat X times
    val results = indexes.map( ndx => {

      // Wrap the whole call with the timer
      wrap[T]( desc, ndx, () => {

        // Attempt ( try/catch ) the callback which returns Result[T]
        Funcs.attempt[T]( () => callback(ndx) )
      })
    })

    // First / last
    val start = results.head.start
    val end = results.last.end

    // Duration of whole thing
    val duration = start.durationFrom(end)

    // Average them out
    val totalMs = results.foldLeft(0L)((total, res) => total + res.duration.toMillis )
    val avgMs = totalMs / count

    // Finally the result
    ResultTimed[T](desc, results.head.start, results.last.end, duration, results.last.result,
    results.last.memory, Some(avgMs))
  }


  def wrap[T](desc:String, count:Int, callback:() => Result[T]): ResultTimed[T] = {

    // Start time & memory info
    val started = now()
    val runtime = Runtime.getRuntime
    val freeBefore = runtime.freeMemory()

    val result = callback()

    // End time and memory info
    val ended = now()
    val freeAfter = runtime.freeMemory()

    // Diff: Time
    val duration = started.durationFrom(ended)

    // Avg : Time
    val ms = duration.toMillis

    // Diff: memory
    val used = freeBefore - freeAfter
    val memory = MemUsage(used, runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory())

    new ResultTimed[T](desc, started, ended, duration, result, Some(memory), Some(ms))
  }
}
