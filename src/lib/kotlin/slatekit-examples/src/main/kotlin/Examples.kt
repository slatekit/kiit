import kotlinx.coroutines.*
import java.nio.file.Paths
//import java.util.logging.*

//import org.apache.logging.log4j.LogManager
//import org.apache.logging.log4j.Logger
//import slatekit.common.log.LoggerConsole


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import slatekit.cache.CacheSettings
import slatekit.cache.SimpleCache
import slatekit.common.DateTime
import slatekit.common.ext.toStringUtc
import slatekit.examples.Example_Email
import slatekit.examples.Example_Sms
import slatekit.functions.common.FunctionMode
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.results.builders.Tries
import java.io.File
import java.util.*

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
    //testLRU()
    //testPaths()

    val example = Example_Sms()
    example.execute(args)
}


fun testPaths(){
    val roots = File.listRoots()
    println(roots.first().absolutePath)

    val items = listOf(
            "/Users/kishore.reddy/dev/tmp/out.txt",
            "~/dev/tmp/out.txt",
            "./dev/tmp/out.txt",
            ".conf/dev/tmp/out.txt",
            "../dev/tmp/out.txt",
            "\$tmp:///dev/tmp/out.txt",
            "jar://dev/tmp/out.txt"
    )
    val f = File("~/dev/tmp/out.txt")
    println(f.absolutePath)
    println(f.canonicalPath)
    println(f.readText())

    val results = items.map {
        val path = Tries.of { Paths.get(it) }
        val file = Tries.of { File(it) }
        Triple(it, path, file)
    }
    results.map {
        println("\n")
        println("text: ${it.first}" )
        println("path.abs: ${it.second.fold({ Tries.of{ it.toAbsolutePath()}.fold({ it }, { "FAILED" })}, { "ERROR" })}" )
        println("path.rea: ${it.second.fold({ Tries.of{ it.toRealPath()    }.fold({ it }, { "FAILED" })}, { "ERROR" })}" )
        println("path.uri: ${it.second.fold({ Tries.of{ it.toUri()         }.fold({ it }, { "FAILED" })}, { "ERROR" })}" )
        println("path.nor: ${it.second.fold({ Tries.of{ it.normalize()     }.fold({ it }, { "FAILED" })}, { "ERROR" })}" )
        println("file.abs: ${it.third .fold({ it.absolutePath } , { "ERROR" })}" )
        println("file.can: ${it.third .fold({ it.canonicalPath }, { "ERROR" })}" )
    }
    println("\nDONE")
}


fun testLRU() {
    val cache = SimpleCache(CacheSettings(3))
    cache.put("a", "desc a", 500) { 1 }
    cache.put("b", "desc a", 500) { 2 }
    cache.put("c", "desc a", 500) { 3 }
    cache.put("d", "desc a", 500) { 4 }
    cache.put("e", "desc a", 500) { 5 }

    println(cache.get<String>("e"))
    println(cache.get<String>("d"))
    println(cache.get<String>("c"))
    println(cache.get<String>("a"))
    println(cache.get<String>("b"))
}


suspend fun testDefer(){
    val deferred = testRequest()
    val result = deferred.await()
    println(result)
}

data class Req(val id:String, val deferred:CompletableDeferred<String>)


suspend fun testRequest():Deferred<String> {
    println(DateTime.now().toStringUtc())
    val req = Req(UUID.randomUUID().toString(), CompletableDeferred<String>())
    testProcess(req)
    return req.deferred
}


suspend fun testProcess(req:Req) {
    delay(5000)
    req.deferred.complete("value 123")
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