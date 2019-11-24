import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import slatekit.examples.*
import java.nio.file.Paths
//import java.util.logging.*

//import org.apache.logging.log4j.LogManager
//import org.apache.logging.log4j.Logger
//import slatekit.common.log.LoggerConsole


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import slatekit.providers.logs.logback.LogbackLogs
import kotlinx.coroutines.runBlocking
import slatekit.functions.common.FunctionMode

/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
/**
 * Created by kishorereddy on 6/4/17.
 */

// https://looksok.wordpress.com/2014/07/12/compile-gradle-project-with-another-project-as-a-dependency/
fun main(args:Array<String>) {
    val example = Guide_Cache()
    example.execute(args, FunctionMode.Called)
}


fun testLogs() {
    println(Paths.get(""))


    fun testlog(logger:slatekit.common.log.Logger) {
        val name = logger.name
        //logger.trace("test logging $name with trace")
        logger.debug("Async test 5 logging $name with debug")
        logger.info ("Async test 5 logging $name with info")
        logger.warn ("Async test 5 logging $name with warn")
        logger.error("Async test 5 logging $name with error")

    }

    val log = org.slf4j.LoggerFactory.getLogger("api")
    log.warn("slf4j warn")
    log.error("slf4j error")

    val logs = LogbackLogs()

    val logger1 = logs.getLogger("main")
    val logger2 = logs.getLogger("api" )
    val logger3 = logs.getLogger("db"  )
    testlog(logger1)
    testlog(logger2)
    testlog(logger3)

    Thread.sleep(5000)
    println("hello kotlin examples")
}

/*
// https://looksok.wordpress.com/2014/07/12/compile-gradle-project-with-another-project-as-a-dependency/
fun main(args:Array<String>):Unit  {

    println(Paths.get(""))

    fun testlog(logger:org.slf4j.Logger) {
        val name = logger.name
        logger.trace("test logging $name with trace")
        logger.debug("test logging $name with debug")
        logger.info ("test logging $name with info")
        logger.warn ("test logging $name with warn")
        logger.error("test logging $name with error")
    }

    val logger1 = getLogger("main", "/Users/kishorereddy/git/slatekit/test")
    val logger2 = getLogger("api", "/Users/kishorereddy/git/slatekit/test")
    val logger3 = getLogger("db", "/Users/kishorereddy/git/slatekit/test")
    testlog(logger1)
    testlog(logger2)
    testlog(logger3)

    println("hello kotlin examples")
}
*/

fun getLogger(name:String, path:String): Logger {
    return LoggerFactory.getLogger(name)
    //return LogManager.getLogger(name)
}


/*
fun getLogger(name:String, path:String):Logger {
    val logger = java.util.logging.Logger.getLogger("main")
    logger.level = java.util.logging.Level.INFO
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
    val timestamp = LocalDateTime.now().format(dateFormatter)
    val file = File(path, "${name}-${timestamp}.log")
    val fh = FileHandler(file.toString())
    fh.setFormatter(MyFormatter())
    logger.addHandler(fh)
    return logger
}


class MyFormatter : Formatter() {
    // Create a DateFormat to format the logger timestamp.
    val df = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm-ss")


    override fun format(record: LogRecord):String  {
        val builder = StringBuilder(1000)
        val timestamp = LocalDateTime.now().format(df)
        builder.append("[").append(record.getLevel()).append("] ")
        builder.append(timestamp).append(" : ")
        //builder.append("[").append(record.getSourceClassName()).append(".")
        //builder.append(record.getSourceMethodName()).append("] - ")
        builder.append(formatMessage(record))
        builder.append(newline)
        return builder.toString()
    }
}
*/