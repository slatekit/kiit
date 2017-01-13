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

package slate.common.logging


import slate.common.DateTime
import scala.reflect.runtime.universe.Type

abstract class LoggerBase(val level:LogLevel       = LogLevel.Warn,
                          val name:String          = "",
                          val logType:Option[Type] = None ) extends LogSupport
{

  /**
   * gets a new instance of logger with the supplied type, name and level
   * with the same level as this one by default
   * @param t
   * @param name
   * @param lvl
   * @return
   */
  def getLogger(lvl:Option[LogLevel],  name:String, t:Option[Type] ):LoggerBase = {
    new LoggerConsole(lvl.getOrElse(this.level), name, t)
  }


  /**
   * Logs an entry
 *
   * @param level
   * @param msg
   * @param ex
   */
  override def log(level: LogLevel, msg: String, ex: Option[Exception] = None, tag: Option[String] = None): Unit =
  {
    checkLog(level, {
      performLog(buildLog(level, msg, ex, tag))
    })
  }


  /**
    * Logs an entry
    */
  def log(entry:LogEntry): Unit =
  {
    checkLog(level, {
      performLog(entry)
    })
  }


  /**
    * Logs an entry
    *
    * @param level
    * @param msg
    * @param ex
    */
  protected def buildLog(level: LogLevel, msg: String, ex: Option[Exception] = None, tag: Option[String] = None):
  LogEntry = {
    new LogEntry(name, level, msg, DateTime.now, ex, tag)
  }


  protected def checkLog(level:LogLevel, callback: =>Unit ):Unit = {
    if(level >= this.level) {
      callback
    }
  }


  /**
    * Logs an entry
    *
    * @param entry
    */
  protected def performLog(entry:LogEntry):
  Unit
}
