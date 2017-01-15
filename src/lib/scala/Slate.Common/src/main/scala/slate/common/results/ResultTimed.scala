/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
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
                        result      : Result[T],
                        memory      : Option[MemUsage],
                        avg         : Option[Duration] = None
                       )
{
  val dur = start.durationFrom(end)

  /**
   *
   * @param start
   * @param end
   * @param duration
   * @param mem
   * @param data
   */
  def this(start:DateTime, end:DateTime, duration:Duration, data:Result[T], mem:Option[MemUsage], avg:Option[Duration])
  {
    this("", start, end, duration, data, mem, avg)
  }


  def print():Unit = {
    println("desc           : " + desc               )
    println("start          : " + start              )
    println("end            : " + end                )
    println("duration.secs  : " + duration.getSeconds)
    println("duration.nanos : " + duration.getNano   )

    memory.map(mem => {
      println("memory.used    : " + mem.used)
      println("memory.free    : " + mem.free)
      println("memory.total   : " + mem.total)
      println("memory.max     : " + mem.max)
      Unit
    })
    println()
  }


  def toJson():String = {
    "not implemented"
  }
}


object ResultTimed {

  def build[T](started:DateTime, result:Result[T] ):ResultTimed[T] = {
    val ended = DateTime.now()
    val duration = started.durationFrom(ended)
    new ResultTimed[T]("", started, ended, duration, result, None, None)
  }
}
