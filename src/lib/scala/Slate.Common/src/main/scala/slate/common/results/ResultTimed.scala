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
  extends ResultBase
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


  override def print():Unit = {
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


  override def toJson():String = {
    val json = new ObjectBuilderJson(true, "  ")
    json.begin()
    json.putString("success"  , if( success ) "true" else "false"     )
    json.putString("msg"      , msg.getOrElse("null")   )
    json.putString("code"     , code.toString  )
    json.putString("data"     , data.map[String]( d => d.toString ).getOrElse("null"))
    json.putString("err"      , err.map[String]( e => e.getMessage ).getOrElse("null"))
    json.putString("ext"      , ext.getOrElse("null").toString )
    json.putString("tag"      , tag.getOrElse("null") )
    json.end()
    val text = json.toString()
    text
  }
}
