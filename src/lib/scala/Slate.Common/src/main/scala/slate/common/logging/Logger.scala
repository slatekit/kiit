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


import slate.common.logging.LogLevel.LogLevel


object Logger extends LoggerBase {

  private var _logger:Option[LoggerBase] = None


  /**
   * Initializes the logger
   * @param level: The level of the logger
   * @param logger: The logger
   */
  def init(level: LogLevel, logger: LoggerBase) =
  {
    _logger = Some(logger)
  }


  /**
   * Logs a message
   * @param level: The log level
   * @param msg: The message
   * @param ex: The exception
   */
  override protected def performLog(level: LogLevel, msg: String, ex: Option[Exception] = None, tag: Option[String] = None) =
  {
    try
    {
      if(_logger.isDefined)
      {
        _logger.map( (l) => { l.log(level, msg, ex); true } )
      }
    }
    catch
    {
      case e: NullPointerException =>
      {
        println("Error logging : null pointer exception")
      }
      case e:Exception =>
      {
        println("Error logging : unexpected")
      }
    }
    finally
    {
      //println("done logging")
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
