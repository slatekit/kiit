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


import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.server.engine.*

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        install(ContentNegotiation)
        routing {
            get("/") {
                //call.respondText("", ContentType.Application.Json)
                respond(call)
            }
            get("/app/api/*") {
                //call.respondText("", ContentType.Application.Json)
                respond(call)
            }
            post("/types") {
                val t = call.receive<TestTypes>()
                call.respondText(t.toString(), ContentType.Text.Plain)
            }
            post("/app/api/*") {
                //call.respondText("", ContentType.Application.Json)
                respond(call)
            }
        }
    }.start(wait = true)
}

data class TestTypes(val typeStr: String, val typeInt:Int, val typeLong:Long, val typeBool:Boolean)

suspend fun respond(call:ApplicationCall) {
    val headers = call.request.headers.names().joinToString(",")
    val req = call.request
    val path = req.path()
    val method = req.httpMethod.toString()
    val uri = req.uri
    val host = req.host()
    val version = req.httpVersion
    val contentType = req.contentType()
    val isMultipart = req.isMultipart()
    val text = if(req.httpMethod == HttpMethod.Post) call.receiveText() else "null"

    val json = """{
                    "path"       : "$path",
                    "uri"        : "$uri",
                    "method"     : "$method",
                    "host"       : "$host",
                    "version"    : "$version",
                    "contentType": "$contentType",
                    "headers"    : "$headers",
                    "isMultiPart": "$isMultipart",
                    "body"       : $text
                  }
                """
    call.respondText(json, ContentType.Application.Json)
}


//fun coroutines():Unit {
//    println("starting...")
//
//    // Start a coroutine
//    val j1 = launch {
//        delay(1000)
//        println("Hello")
//    }
//
//
//    val j2 = launch {
//        val result = increment(40)
//        println(result)
//    }
//
//
//    j1.invokeOnCompletion {
//        println("done with job 1")
//    }
//
//
//
//    Thread.sleep(2000) // wait for 2 seconds
//    println("Stopping...")
//}
//
//
//suspend fun increment(x:Int):Int {
//    var result = 0
//    for(i in 1..x){
//        result += i
//    }
//    return result
//}
//
//
//suspend fun test():Unit {
//
//    val c = AtomicInteger()
//
//    for (i in 1..10) {
//        launch {
//            c.addAndGet(i)
//        }
//    }
//    println(c.get())
//}


