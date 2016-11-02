/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.results

import java.time.Duration

import slate.common._
import slate.common.info.MemUsage

case class ResultTimed[+T](
                        desc        : String,
                        start       : DateTime,
                        end         : DateTime,
                        duration    : Duration,
                        memory      : MemUsage,
                        result      : Result[T],
                        avg         : Duration
                       )
{
  /**
   *
   * @param start
   * @param end
   * @param duration
   * @param mem
   * @param data
   */
  def this(start:DateTime, end:DateTime, duration:Duration, mem:MemUsage, data:Result[T], avg:Duration)
  {
    this("", start, end, duration, mem, data, avg)
  }


  def withData(result:Any):ResultTimed[T] = {
    if(result == null){
      return this
    }
    if(result.isInstanceOf[ResultTimed[Any]]) {
      return new ResultTimed[T] (start, end, duration, memory, result.asInstanceOf[Result[T]], avg)
    }
    if(result.isInstanceOf[Result[Any]]) {
      return new ResultTimed[T] (start, end, duration, memory, result.asInstanceOf[Result[T]], avg)
    }
    new ResultTimed[T](start, end, duration, memory, NoResult, avg)
  }


  def print():Unit = {
    println("desc           : " + desc               )
    println("start          : " + start              )
    println("end            : " + end                )
    println("duration.secs  : " + duration.getSeconds)
    println("duration.nanos : " + duration.getNano   )
    println("memory.used    : " + memory.used        )
    println("memory.free    : " + memory.free        )
    println("memory.total   : " + memory.total       )
    println("memory.max     : " + memory.max         )
    println()
  }


  def toJson():String = {
    "not implemented"
  }
}
