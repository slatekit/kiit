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

case class ResultTimed[+T](
                        desc    :String,
                        start   : DateTime,
                        end     : DateTime,
                        duration: Duration,
                        memory  : MemUsage,
                        result  : Result[T]
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
  def this(start:DateTime, end:DateTime, duration:Duration, mem:MemUsage, data:Result[T])
  {
    this("", start, end, duration, mem, data)
  }


  def withData(result:Any):ResultTimed[T] = {
    if(result == null){
      return this
    }
    if(result.isInstanceOf[ResultTimed[Any]]) {
      return new ResultTimed[T] (start, end, duration, memory, result.asInstanceOf[Result[T]])
    }
    if(result.isInstanceOf[Result[Any]]) {
      return new ResultTimed[T] (start, end, duration, memory, result.asInstanceOf[Result[T]])
    }
    new ResultTimed[T](start, end, duration, memory, NoResult)
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
