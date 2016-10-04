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
import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.util.concurrent.TimeUnit

import slate.common.results.{ResultSupportIn, ResultCode, ResultTimed}

import scala.collection.mutable.ListBuffer


object Timer extends ResultSupportIn
{

  /**
    * benchmarks an operation once
 *
    * @param desc     : description of the operation
    * @param callback : the callback to execute
    * @return
    */
  def once(desc:String, callback: => Unit): ResultTimed[Any] = {

    val started = DateTime.now()
    var msg = ""
    var success = false
    var used = 0L
    val runtime = Runtime.getRuntime
    val freeBefore = runtime.freeMemory()
    try
    {
      callback
      val freeAfter = runtime.freeMemory()
      used = freeBefore - freeAfter
      success = true
    }
    catch
    {
      case ex:Exception =>
      {
        success = false
        msg = s"Error during benchmarking of : $desc"
        val freeAfter = runtime.freeMemory()
        used = freeBefore - freeAfter
      }
    }
    val memory = MemUsage(used, runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory())
    val ended = DateTime.now()
    val duration = started.durationFrom(ended)
    val result = okOrFailure(success, Some(msg))
    new ResultTimed[Any](desc, started, ended, duration, memory, result)
  }


  /**
    * benchmarks an operation several times and returns a list of benchmarked results
 *
    * @param desc     : description of the operation
    * @param callback : the callback to execute
    * @param count    : the number of times to run
    * @return
    */
  def many(desc:String, callback:(Int) => Unit, count:Int): List[ResultTimed[Any]] = {

    val results = ListBuffer[ResultTimed[Any]]()
    for (c <- 1 to count) {
      val result = once(desc, () =>
      {
        callback(c)
      })
      results.append(result)
    }
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
  def avg(desc:String, callback:(Int) => Unit, count:Int): ResultTimed[Any] = {
    val results = ListBuffer[ResultTimed[Any]]()
    val started = DateTime.now
    val runtime = Runtime.getRuntime
    val freeBefore = runtime.freeMemory()

    for (c <- 1 to count) {
      callback(c)
    }

    val freeAfter = runtime.freeMemory()
    val ended = DateTime.now
    val used = freeBefore - freeAfter
    val memory = MemUsage(used, runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory())
    val duration = started.durationFrom(ended)
    val averageNano = duration.getNano / count
    val averageDuration = Duration.of(averageNano, ChronoUnit.NANOS)
    val result = okOrFailure(true)
    new ResultTimed(desc, started, ended, averageDuration, memory, result)
  }
}
