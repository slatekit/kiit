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

import java.time.LocalTime

/**
 * Mimicks a C# time span
 * @param time
 */
class TimeSpan (time:LocalTime)
{
  val hours = time.getHour
  val minutes = time.getMinute
  val seconds = time.getSecond
  val milliseconds = time.getNano
  val raw = time

  def this(hours: Int, minutes: Int, seconds: Int, milliseconds:Int ) = {
   this(LocalTime.of(hours, minutes, seconds))
  }


  def this( hours: Int, minutes: Int, seconds: Int)  =
  {
    this(LocalTime.of(hours, minutes, seconds))
  }


  def toNumericValue: Int = {
    val txt = toStringNumeric()
    txt.toInt
  }


  def toStringNumeric() : String =
  {
    val time = "" +
               (if(hours   < 10 ) "0" + hours else hours )    +
               (if(minutes < 10 ) "0" + minutes else minutes) +
               (if(seconds < 10 ) "0" + seconds else seconds)
    time
  }
}


object TimeSpan
{

  def apply(hours: Int, minutes: Int, seconds: Int) : TimeSpan =
  {
    new TimeSpan(hours, minutes, seconds, 0)
  }


  def apply(time:LocalTime) : TimeSpan =
  {
    new TimeSpan(time)
  }
}
