package slatekit.server


import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import slatekit.actors.*
import slatekit.common.Identity
import slatekit.jobs.*
import slatekit.jobs.support.Events
import java.util.concurrent.atomic.AtomicLong


lateinit var id: Identity
lateinit var wrk: Emailer
lateinit var mgr: Manager
lateinit var iss: Issuer<Task>


fun main(args:Array<String>) {
    runBlocking {
        id = Identity.job("account", "signup")
        wrk = Emailer(id)
        mgr = Manager(id, wrk, middleware = Printer(), settings = Settings(false, false))
        iss = Issuer<Task>(mgr.channel, mgr)
        val job = mgr.work()
        serve()
    }
}


class Printer : Middleware {
    override suspend fun handle(mgr: Manager, source: String, message: Message<*>, next: suspend (Message<*>) -> Unit) {
        //message.print()
        val action = when(message){
            is Control -> message.action.name.toUpperCase()
            is Content -> Action.Process.name.toUpperCase()
            is Request -> "LOAD"
        }
        next(message)
        val value = wrk.count.get().toString()
        Events.record(mgr, action, message, value)
    }
}


suspend fun test() {
    mgr.start()
    iss.pull()
    iss.pull()
    iss.pull()
    println("DONE")
    delay(20000)
}



/**
 * Simple Email Worker
 */
class Emailer(id:Identity) : Worker<String>(id) {
    var finished = false
    val count = AtomicLong(0L)

    override suspend fun work(task:Task): WResult {
        if(finished) return WResult.Done
        val curr = count.incrementAndGet()
        delay(4000)
        //println("Worker id=${this.id.instance}, value=${curr}")
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
                mgr.start()
                call.respondText("job.start")
            }
            get ( "/job/stop" ) {
                mgr.stop()
                call.respondText("job.stop")
            }
            get ( "/job/pause" ) {
                mgr.pause()
                call.respondText("job.pause")
            }
            get ( "/job/resume" ) {
                mgr.resume()
                call.respondText("job.resume")
            }
            get ( "/job/kill" ) {
                mgr.kill()
                call.respondText("job.kill")
            }
            get ( "/job/value" ) {
                call.respondText("job.value = ${wrk.count}")
            }
            get ( "/job/status" ) {
                call.respondText("job.status = ${mgr.status().name}")
            }
        }
    }.start(wait = true)
}