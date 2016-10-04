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


import slate.common.ConsoleWriter
import slate.common.logging.LogLevel.LogLevel


class LoggerConsole(level:LogLevel = LogLevel.Debug)
  extends LoggerBase(level, "console") {

  private val _writer = new ConsoleWriter()


  /**
   * Logs to the console
   * @param level :
   * @param msg   :
   * @param ex    :
   * @param tag   :
   */
  override protected def performLog(level: LogLevel, msg: String,
                   ex: Option[Exception] = None, tag: Option[String] = None) =
  {
    level match {
      case LogLevel.Debug => _writer.subTitle(level.toString + " : " + msg)
      case LogLevel.Info  => _writer.text(level.toString + "  : " + msg)
      case LogLevel.Warn  => _writer.url(level.toString + "  : " + msg)
      case LogLevel.Error => _writer.error(level.toString + " : " + msg)
      case LogLevel.Fatal => _writer.highlight(level.toString + " : " + msg)
      case _              => _writer.text(level.toString + " : " + msg)
    }
  }
}
