package slatekit.samples.api


// Ktor
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import slatekit.apis.*
import slatekit.apis.routes.Api

// Slate Kit - Common Utilities
import slatekit.common.*
import slatekit.results.*

// Slate Kit - App ( provides args, help, life-cycle methods, etc )
import slatekit.common.ext.toResponse
import slatekit.context.Context

// Slate Kit - Server ( Ktor support )
import slatekit.server.ServerSettings
import slatekit.server.ktor.KtorHandler
import slatekit.server.ktor.KtorResponse

// Sample App
import slatekit.samples.common.apis.SampleApi
import slatekit.samples.common.auth.SampleAuth


class Server(val ctx: Context)  {

    /**
     * executes the app
     *
     * @return
     */
    suspend fun execute(): Try<Any> {

        // 1. Settings
        val settings = ServerSettings(port = 5000, prefix = "/api/", docs = true, docKey = "abc123")

        // 2. APIs ( these are Slate Kit Universal APIs )
        val apis = apis()

        // 3. Authenticator
        val auth = SampleAuth()

        // 4. API host
        val apiHost = ApiServer.of( ctx, apis, auth = null)

        // Ktor response handler
        val responder = KtorResponse()

        // Ktor request handler
        val handler = KtorHandler(ctx, settings, apiHost, responses = responder)

        // Ktor
        val server = embeddedServer(Netty, settings.port) {
            routing {

                // Root
                get("/") {
                    ping(call, responder)
                }

                // Your own custom path
                get(settings.prefix + "/ping") {
                    ping(call, responder)
                }

                // Your own multi-path route
                get("module1/feature1/action1") {
                    responder.json(call, Success("action 1 : " + DateTime.now().toString()).toResponse())
                }

                // Remaining outes beginning with /api/ to be handled by Slate Kit API Server
                handler.register(this)
            }
        }

        // CORS
        // server.application.install(CORS)

        // Start server
        server.start(wait = true)
        return Success(true)
    }


    fun apis(): List<Api> {
        return listOf(
                Api(klass = SampleApi::class, singleton = SampleApi(ctx), setup = SetupType.Annotated)
        )
    }


    /**
     * pings the server to only get back the datetime.
     * Used for quickly checking a deployment.
     */
    suspend fun ping(call: ApplicationCall, responses: KtorResponse) {
        val result = "Version ${ctx.info.build.version} : " + DateTime.now()
        responses.json(call, Success(result).toResponse())
    }
}