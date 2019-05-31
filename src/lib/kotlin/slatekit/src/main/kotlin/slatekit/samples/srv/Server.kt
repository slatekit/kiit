package slatekit.samples.srv


// Ktor
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import slatekit.apis.ApiHost
import slatekit.apis.core.Annotated
import slatekit.apis.doc.DocWeb
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.security.WebProtocol

// Slate Kit - Common Utilities
import slatekit.common.*
import slatekit.common.encrypt.*
import slatekit.results.*

// Slate Kit - App ( provides args, help, life-cycle methods, etc )
import slatekit.common.auth.Roles
import slatekit.common.metrics.MetricsLite
import slatekit.common.requests.Request
import slatekit.meta.Deserializer

// Slate Kit - Server ( Ktor support )
import slatekit.server.ServerConfig
import slatekit.server.common.ServerDiagnostics
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
        val config = ServerConfig(port = 5000, prefix = "/api/", docs = true, docKey = "abc123")

        // 2. APIs ( these are Slate Kit Universal APIs )
        val apis = apis()

        // 3. Authenticator
        val auth = SampleAuth()

        // 4. API host
        val apiHost = ApiHost( ctx, false, auth, WebProtocol,
                apis = apis,
                docKey = config.docKey,
                docBuilder = { DocWeb() },
                deserializer = { req: Request, enc:Encryptor? -> Deserializer(req, enc) }
        )

        // Ktor handler
        val metrics = MetricsLite()
        val diagnostics = ServerDiagnostics("app", ctx.logs.getLogger("app"), metrics, listOf())
        val handler = KtorHandler(ctx, config, apiHost, diagnostics, KtorResponse)

        // Ktor
        val server = embeddedServer(Netty, config.port) {
            routing {

                // Root
                get("/") {
                    ping(call)
                }

                // Your own custom path
                get(config.prefix + "/ping") {
                    ping(call)
                }

                // Your own multi-path route
                get("module1/feature1/action1") {
                    KtorResponse.json(call, Success("action 1 : " + DateTime.now().toString()).toResponse())
                }

                // Remaining outes beginning with /api/ to be handled by Slate Kit API Server
                handler.register(this)
            }
        }

        // CORS
        if (config.cors) {
            server.application.install(CORS)
        }

        // Start server
        server.start(wait = true)
        return Success(true)
    }


    fun apis(): List<slatekit.apis.core.Api> {
        return listOf(
                slatekit.apis.core.Api(
                        cls = SampleApi::class,
                        setup = Annotated,
                        declaredOnly = true,
                        auth = AuthModes.apiKey,
                        roles = Roles.all,
                        verb = Verbs.auto,
                        protocol = Protocols.all,
                        singleton = SampleApi(ctx)
                )
        )
    }


    /**
     * pings the server to only get back the datetime.
     * Used for quickly checking a deployment.
     */
    suspend fun ping(call: ApplicationCall) {
        val result = "Version ${ctx.app.version} : " + DateTime.now()
        KtorResponse.json(call, Success(result).toResponse())
    }
}