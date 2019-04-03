package slatekit.server.sample

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import slatekit.apis.ApiHost
import slatekit.common.DateTime
import slatekit.common.info.Info
import slatekit.common.toResponse
import slatekit.results.Success
import slatekit.server.ktor.KtorHandler
import slatekit.server.ktor.KtorResponse


//fun main(args:Array<String>){
//    SampleServer(args).run()
//}


class SampleServer(val args:Array<String>) {

    /**
     * Server the Slate KIT APIs using Ktor
     */
    fun run() {

        // Basic setup
        val config = SampleSetup.config
        val context = SampleSetup.context
        val diagnostics = SampleSetup.diagnostics
        val apis = SampleSetup.apis
        val auth = SampleAuth()
        val container = SampleSetup.container(auth)

        // Ktor handler
        val handler = KtorHandler(context, config, container, diagnostics, KtorResponse)

        // Ktor
        val server = embeddedServer(Netty, config.port) {
            routing {
                get("/") {
                    ping(call)
                }
                get(config.prefix + "/ping") {
                    ping(call)
                }
                get("module1/feature1/action1") {
                    KtorResponse.json(call, Success("action 1 : " + DateTime.now().toString()).toResponse())
                }

                // Allow slatekit server to handle routes beginning with /api/
                handler.register(this)
            }
        }

        // CORS
        if (config.cors) {
            server.application.install(CORS)
        }

        val info = Info(context.app, context.build, context.start, context.sys)
        info.each({ key, value ->  println("$key : $value") })
        println("server.port : ${config.port}")
        server.start(wait = true)
    }



    /**
     * pings the server to only get back the datetime.
     * Used for quickly checking a deployment.
     */
    suspend fun ping(call: ApplicationCall) {
        val result = "Version 1 : " + DateTime.now()
        KtorResponse.json(call, Success(result).toResponse())
    }
}