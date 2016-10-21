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


package slate.examples

//<doc:import_required>
import slate.common.logging.LogLevel.LogLevel
import slate.common.logging._
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
import slate.common.results.ResultSupportIn
//</doc:import_examples>

/**
 * Created by kv on 10/18/2015.
 */
class Example_Logger extends Cmd("types") with ResultSupportIn
  with LogSupportIn
{
  //<doc:setup>

  // NOTE: The logger is very simple and designed to be extended for customization
  // especially to use a more robust logging system such as :
  // 1. log4net
  // 2. loggly
  // 3. new relic
  //
  // Setup the static logger with implementation for logging to console.
  Logger.init(LogLevel.Info, new LoggerConsole())

  // Setup a custom logger
  class MyCustomLogger extends LoggerBase {

    override def performLog(level: LogLevel, msg: String, ex: Option[Exception], tag: Option[String])
      : Unit =
    {
      println("custom logger : " + level + " : " + msg)
    }
  }
  //</doc:setup>

  def execute() =
  {

    //<doc:examples>
    // Sample exception
    val ex = new IllegalArgumentException("Example exception")

    // CASE 1: Different ways to log with the static logger
    // 1. message only
    // 2. message + exception
    // 3. message + exception + tag
    Logger.debug("debug with message only")
    Logger.info("info with message and exception", Some(ex))
    Logger.warn("debug with message, exception, and tag", Some(ex), Some("APP1") )


    // CASE: 2 Standard info, warn, error levels available
    // Same overloads ( msg, ex, tag ) are available.
    Logger.info("info")
    Logger.warn("warn")
    Logger.error("error")


    // CASE 3: Log explicitly using log method.
    Logger.log(LogLevel.Error, "error", Some(ex), Some("APP1"))


    // CASE 4: You can extend a class with the LogSupportIn trait
    // to add logging methods to any class. The trait expects to have
    // a _log member variable available.
    _log = Some(new LoggerConsole())
    debug("debug from trait")
    info ("info from trait")
    warn ("warn from trait")
    error("error from trait")


    // CASE 5: Custom logger ( see setup above )
    // YOu just have to implement the log method.
    val log = new MyCustomLogger()
    log.debug("debug from trait")
    log.info ("info from trait")
    log.warn ("warn from trait")
    log.error("error from trait")
    //</doc:examples>
  }
}
