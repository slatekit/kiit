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


object Logger extends LoggerBase {

  private var _logger:Option[LoggerBase] = None


  /**
   * Initializes the logger
   * @param level: The level of the logger
   * @param logger: The logger
   */
  def init(level: LogLevel, logger: LoggerBase) = {
    _logger = Option(logger)
  }


  /**
   * Logs a message
   * @param entry: The log entry
   */
  override protected def performLog(entry:LogEntry) =
  {
    try
    {
      _logger.fold[Unit](None)( l => l.log(entry) )
    }
    catch
    {
      case e:Exception =>
      {
        println("Error logging : unexpected")
      }
    }
  }


  def parseLogLevel(level:String):LogLevel = {
    var logLevel = LogLevel.Debug
    level match {
      case "debug" => logLevel = LogLevel.Debug
      case "info"  => logLevel = LogLevel.Info
      case "warn"  => logLevel = LogLevel.Warn
      case "error" => logLevel = LogLevel.Error
      case _       => logLevel = LogLevel.Warn
    }
    logLevel
  }
}
