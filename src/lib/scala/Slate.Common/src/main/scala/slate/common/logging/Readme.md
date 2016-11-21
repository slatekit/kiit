# Logger

| field | value  | 
|:--|:--|
| **desc** | A simple logger with extensibility for using other 3rd party loggers | 
| **date**| 2016-11-21T16:49:15.693 |
| **version** | 0.9.1  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.logging  |
| **source core** | slate.common.logging.Logger.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/logging](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/logging)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Logger.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Logger.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.logging._


// optional 
import slate.core.cmds.Cmd
import slate.common.results.ResultSupportIn


```

## Setup
```scala



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

    override def performLog(entry:LogEntry)
      : Unit =
    {
      println("custom logger : " + entry.level + " : " + entry.msg)
    }
  }
  

```

## Usage
```scala


    // Sample exception
    val ex = new IllegalArgumentException("Example exception")

    // CASE 1: Different ways to log with the static logger
    // 1. message only
    // 2. message + exception
    // 3. message + exception + tag
    Logger.debug("debug with message only")
    Logger.info("info with message and exception", Some(ex))
    Logger.warn("debug with message, exception, and tag", Some(ex), Some("APP1") )
    Logger.fatal("fatal message", tag = Some("123"))

    // CASE: 2 Standard info, warn, error levels available
    // Same overloads ( msg, ex, tag ) are available.
    Logger.debug("debug")
    Logger.info("info")
    Logger.warn("warn")
    Logger.error("error")
    Logger.fatal("fatal")


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
    fatal("fatal from trait")


    // CASE 5: Custom logger ( see setup above )
    // YOu just have to implement the log method.
    val log = new MyCustomLogger()
    log.debug("debug from trait")
    log.info ("info from trait")
    log.warn ("warn from trait")
    log.error("error from trait")
    log.fatal("fatal from trait")
    

```

