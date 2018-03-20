---
layout: start_page_mods_utils
title: module Logger
permalink: /kotlin-mod-logger
---

# Logger

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A simple logger with extensibility for using other 3rd party loggers | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.log  |
| **source core** | slatekit.common.log.Logger.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Logger.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Logger.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.log.*
import slatekit.common.results.ResultFuncs.ok



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result


```

## Setup
```kotlin


  // NOTE: The logger is very simple and designed to be extended for customization
  // especially to use a more robust logging system such as :
  // 1. log4net
  // 2. loggly
  // 3. new relic
  override val logger:LoggerBase? = LoggerConsole()


  // Setup a custom logger
  class MyCustomLogger : LoggerBase(Warn)  {


    override val logger:LoggerBase? = LoggerConsole()


    override fun performLog(entry: LogEntry): Unit
    {
      println("custom logger : " + entry.level + " : " + entry.msg)
    }
  }


  

```

## Usage
```kotlin


    // Sample exception
    val ex = IllegalArgumentException("Example exception")

    // CASE 1: Different ways to log with the static logger
    // 1. message only
    // 2. message + exception
    // 3. message + exception + tag
    val logger = LoggerConsole(Debug)
    logger.debug("debug with message only")
    logger.info("info with message and exception", ex)
    logger.warn("debug with message, exception, and tag", ex, "APP1")
    logger.fatal("fatal message", tag = "123")

    // CASE: 2 Standard info, warn, error levels available
    // Same overloads ( msg, ex, tag ) are available.
    logger.debug("debug")
    logger.info("info")
    logger.warn("warn")
    logger.error("error")
    logger.fatal("fatal")


    // CASE 3: Log explicitly using log method.
    logger.log(Error, "error", ex, "APP1")


    // CASE 4: You can extend a class with the LogSupportIn trait
    // to add logging methods to any class. The trait expects to have
    // a _log member variable available.
    debug("debug from trait")
    info ("info from trait")
    warn ("warn from trait")
    error("error from trait")
    fatal("fatal from trait")


    // CASE 5: Custom logger ( see setup above )
    // YOu just have to implement the log method.
    val log = MyCustomLogger()
    log.debug("debug from trait")
    log.info ("info from trait")
    log.warn ("warn from trait")
    log.error("error from trait")
    log.fatal("fatal from trait")
    

```

