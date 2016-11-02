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


class LoggerConsole(level:LogLevel = LogLevel.Debug)
  extends LoggerBase(level, "console") {

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
      case LogLevel.Debug => _writer.subTitle  (entry.level.name + " : "  + entry.msg.getOrElse(""))
      case LogLevel.Info  => _writer.text      (entry.level.name + "  : " + entry.msg.getOrElse(""))
      case LogLevel.Warn  => _writer.url       (entry.level.name + "  : " + entry.msg.getOrElse(""))
      case LogLevel.Error => _writer.error     (entry.level.name + " : "  + entry.msg.getOrElse(""))
      case LogLevel.Fatal => _writer.highlight (entry.level.name + " : "  + entry.msg.getOrElse(""))
      case _              => _writer.text      (entry.level.name + " : "  + entry.msg.getOrElse(""))
    }
  }
}
