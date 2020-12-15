package slatekit.server


import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import slatekit.actors.Issuer
import slatekit.actors.Message
import slatekit.common.Identity
import slatekit.jobs.*
import java.util.concurrent.atomic.AtomicLong


lateinit var id: Identity
lateinit var wrk: Emailer
lateinit var mgr: Manager
lateinit var iss: Issuer<Task>


fun main(args:Array<String>) {
    runBlocking {
        id = Identity.job("account", "signup")
        wrk = Emailer(id)
        mgr = Manager(id, wrk, middleware = Printer())
        iss = Issuer<Task>(mgr.channel, mgr)
        mgr.start()
        iss.pull()
        iss.pull()
        println("DONE")
    }
}


class Printer : Middleware {
    override suspend fun handle(mgr: Manager, source: String, message: Message<*>, next: suspend (Message<*>) -> Unit) {
        message.print()
        next(message)
    }
}


suspend fun work(mgr:Manager, wrk:Emailer) {
    wrk.work()
    wrk.work()
    wrk.work()
}



/**
 * Simple Email Worker
 */
class Emailer(id:Identity) : Worker<String>(id) {
    var finished = false
    val count = AtomicLong(0L)

    override suspend fun work(): WResult {
        if(finished) return WResult.Done
        val curr = count.incrementAndGet()
        delay(3000)
        println("Worker id=${this.id.instance}, value=${curr}")
        return WResult.More
    }
}


/**
 * Server to host the jobs
 */
fun serve() {
    embeddedServer(Netty, port = 8000) {
        routing {
            get ("/") {
                call.respondText("Hello, Slate Kit Actors and Jobs!")
            }
            get ( "/job/start" ) {
                call.respondText("job.start")
            }
            get ( "/job/stop" ) {
                call.respondText("job.stop")
            }
            get ( "/job/pause" ) {
                call.respondText("job.pause")
            }
            get ( "/job/resume" ) {
                call.respondText("job.resume")
            }
            get ( "/job/kill" ) {
                call.respondText("job.kill")
            }
            get ( "/job/status" ) {
                call.respondText("job.status")
            }
        }
    }.start(wait = true)
}