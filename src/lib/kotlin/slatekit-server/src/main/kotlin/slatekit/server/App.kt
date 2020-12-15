package slatekit.server


import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main(args:Array<String>) {
}

//lateinit var job:slatekit.jobs.Job

fun work() {
//    job = Job()
}


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