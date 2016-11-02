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

import slate.common.info.MemUsage
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
    new ResultTimed[Any](desc, started, ended, duration, memory, result, duration)
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
  def avg(desc:String, count:Int, callback:(Int) => Unit): ResultTimed[Any] = {
    val result = wrap(desc, count, () =>
    {
      for (c <- 1 to count) {
        callback(c)
      }
    })
    result
  }


  /**
    * benchmarks an operation several times and returns an average duration for each run
 *
    * @param desc     : description of the operation
    * @param callback : the callback to execute
    * @param count    : the number of times to run
    * @return
    */
  def batch(desc:String, batches:Int, count:Int, callback:(Int) => Unit): ResultTimed[Any] = {
    val result = wrap(desc, batches, () =>
    {
      for (c <- 1 to batches) {
        avg(desc, count, callback)
      }
    })
    result
  }


  def wrap(desc:String, count:Int, callback:() => Unit): ResultTimed[Any] = {
    val started = DateTime.now
    val runtime = Runtime.getRuntime
    val freeBefore = runtime.freeMemory()

    callback()

    val freeAfter = runtime.freeMemory()
    val ended = DateTime.now
    val used = freeBefore - freeAfter
    val memory = MemUsage(used, runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory())
    val duration = started.durationFrom(ended)
    val nano:Double = duration.getNano
    val averageNano = nano / count.toDouble
    val averageDuration = Duration.of(averageNano.toInt, ChronoUnit.NANOS)
    val result = okOrFailure(true)
    new ResultTimed(desc, started, ended, duration, memory, result, averageDuration)
  }
}
