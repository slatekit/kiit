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

import slate.common.console.ConsoleWriter
import scala.reflect.runtime.universe.Type

class LoggerConsole(level:LogLevel = Debug,
                    name:String          = "console",
                    logType:Option[Type] = None )
  extends LoggerBase(level, name, logType) {

  private val _writer = new ConsoleWriter()


  /**
   * Logs to the console
 *
   * @param entry :
   */
  override protected def performLog(entry:LogEntry) =
  {
    val prefix = entry.
    level match {
      case Debug => _writer.subTitle  (entry.level.name + " : "  + entry.msg)
      case Info  => _writer.text      (entry.level.name + "  : " + entry.msg)
      case Warn  => _writer.url       (entry.level.name + "  : " + entry.msg)
      case Error => _writer.error     (entry.level.name + " : "  + entry.msg)
      case Fatal => _writer.highlight (entry.level.name + " : "  + entry.msg)
      case _     => _writer.text      (entry.level.name + " : "  + entry.msg)
    }
  }
}
