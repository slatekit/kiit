package slatekit.server


import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import slatekit.actors.*
import slatekit.common.Identity
import slatekit.jobs.*
import slatekit.jobs.Context
import slatekit.jobs.support.Events
import java.util.concurrent.atomic.AtomicLong


lateinit var id: Identity
lateinit var wrk: Emailer
lateinit var mgr: Manager
lateinit var ctx: Context
lateinit var chn: Channel<Message<Task>>
lateinit var iss: Issuer<Task>


fun main(args:Array<String>) {
    runBlocking {
        id = Identity.job("account", "signup")
        wrk = Emailer(id)
        chn = Channel(Channel.UNLIMITED)
        ctx = Context(id, listOf(wrk), channel = chn, middleware = Printer())
        mgr = Manager(ctx, settings = Settings(false, false))
        //mgr = Manager(id, wrk, middleware = Printer(), settings = Settings(false, false))
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

suspend fun process(name:String, call: ApplicationCall, op:suspend () -> Unit) {
    println("GOT : $name")
    op()
    call.respondText(name)
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
                process("job.start", call) {
                    mgr.start()
                }
            }
            get ( "/job/stop" ) {
                process("job.stop", call) {
                    mgr.stop()
                }
            }
            get ( "/job/pause" ) {
                process("job.pause", call) {
                    mgr.pause()
                }
            }
            get ( "/job/resume" ) {
                process("job.resume", call) {
                    mgr.resume()
                }
            }
            get ( "/job/kill" ) {
                process("job.kill", call) {
                    mgr.kill()
                }
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