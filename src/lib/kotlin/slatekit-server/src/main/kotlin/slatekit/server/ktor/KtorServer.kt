/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.server.ktor

import com.codahale.metrics.JmxReporter
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.http.HttpMethod
import io.ktor.request.httpMethod
import io.ktor.request.receiveText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import slatekit.apis.ApiContainer
import slatekit.apis.core.Api
import slatekit.apis.security.WebProtocol
import slatekit.apis.core.Auth
import slatekit.apis.core.Events
import slatekit.apis.doc.DocWeb
import slatekit.common.*
import slatekit.common.app.AppMeta
import slatekit.common.app.AppMetaSupport
import slatekit.common.metrics.Metric
import slatekit.common.metrics.Metrics
import slatekit.common.results.ResultChecks
import slatekit.common.results.ResultCode
import slatekit.common.utils.Tracker
import slatekit.core.common.AppContext
import slatekit.meta.Deserializer
import slatekit.server.ServerConfig
import java.util.concurrent.TimeUnit

class KtorServer(
    val config: ServerConfig,
    val ctx: Context,
    val auth: Auth?,
    val apis: List<Api>,
    val metrics:Metrics,
    val events: Events = Events()
) : AppMetaSupport {

    /**
     * initialize with port, prefix for api routes, and all the dependent items
     */
    constructor(
        port: Int = 5000,
        prefix: String = "",
        info: Boolean = true,
        cors: Boolean = false,
        docs: Boolean = false,
        static: Boolean = false,
        staticDir: String = "",
        docKey: String = "",
        apis: List<Api>,
        auth: Auth? = null,
        setup: ((Any) -> Unit)? = null,
        ctx: Context,
        metrics: Metrics,
        events:Events
    ) :
        this(ServerConfig(port, prefix, info, cors, docs, docKey, static, staticDir, setup), ctx, auth, apis, metrics, events)

    val container = ApiContainer(ctx,
        false,
        auth,
        WebProtocol,
        apis,
        deserializer = { req, enc -> Deserializer(req, enc) },
        docKey = config.docKey,
        docBuilder = ::DocWeb)

    override fun appMeta(): AppMeta = ctx.app

    val log = ctx.logs.getLogger("slatekit.server.api")

    val tracker = Tracker<Request, Request, Any, Exception>(Random.guid(), ctx.app.about.name)

    val noException = Exception("none")

    /**
     * executes the application
     * @return
     */
    fun run() {

        val server = embeddedServer(Netty, config.port) {

            //// Metrics using DropWizard
            //install(io.ktor.metrics.Metrics) {
            //    JmxReporter.forRegistry(registry)
            //            .convertRatesTo(TimeUnit.SECONDS)
            //            .convertDurationsTo(TimeUnit.MILLISECONDS)
            //            .build()
            //            .start()
            //}
            routing {
                get("/") {
                    ping(call)
                }
                get(config.prefix + "/ping") {
                    ping(call)
                }
                get(config.prefix + "/*/*/*") {
                    exec(call)
                }
                post(config.prefix + "/*/*/*") {
                    exec(call)
                }
                put(config.prefix + "/*/*/*") {
                    exec(call)
                }
                patch(config.prefix + "/*/*/*") {
                    exec(call)
                }
                delete(config.prefix + "/*/*/*") {
                    exec(call)
                }
            }
        }

        // Display startup
        if (config.info) {
            this.info()
        }

        // CORS
        if (config.cors) {
            server.application.install(CORS)
        }

        server.start(wait = true)
    }

    /**
     * stops the server ( this is not currently accessible on the command line )
     */
    fun stop() {

    }

    /**
     * pings the server to only get back the datetime.
     * Used for quickly checking a deployment.
     */
    suspend fun ping(call: ApplicationCall) {
        val result = DateTime.now()
        KtorResponse.json(call, Success(result).toResponse())
    }

    /**
     * handles the core logic of execute the http request.
     * This is actually accomplished by the SlateKit API Container
     * which handles abstracted Requests and dispatches them to
     * Slate Kit "Protocol Independent APIs".
     */
    suspend fun exec(call: ApplicationCall) {
        val body = when (call.request.httpMethod) {
            HttpMethod.Post -> call.receiveText()
            HttpMethod.Put -> call.receiveText()
            HttpMethod.Patch -> call.receiveText()
            HttpMethod.Delete -> call.receiveText()
            else -> ""
        }

        // 1. Convert the http request to a SlateKit Request
        val request = KtorRequest.build(ctx, body, call, config)

        // 3. Execute the API call
        // The SlateKit ApiContainer will handle the heavy work of
        // 1. Checking routes to area/api/actions ( methods )
        // 2. Validating parameters to methods
        // 3. Decoding request to method parameters
        // 4. Executing the method
        // 5. Handling errors
        val result = container.call(request)

        // Log results
        log(container, request, result)

        // Track the last results for diagnostics
        track(container, request, result)

        // Update metrics
        meter(container, request, result)

        // Notify potential listeners
        notify(container, request, result)

        // Finally convert the result back to a HttpResult
        KtorResponse.result(call, result)
    }

    /**
     * prints the summary of the arguments
     */
    fun info() {
        println("===============================================================")
        println("STARTING : ")
        this.appLogStart({ name: String, value: String -> println(name + " = " + value) })
        println("===============================================================")
    }


    /**
     * Logs the result of a processed job
     */
    fun log(sender: Any, request: Request, result: Response<*>) {
        val info =  "${request.path}, tag: ${request.tag}, result: ${result.code}, msg: ${result.msg}"
        when {
            ResultChecks.isSuccessRange(result.code)    -> log.info ("Request succeeded: $info")
            ResultChecks.isFilteredOut(result.code)     -> log.info ("Request filtered:  $info")
            ResultChecks.isBadRequestRange(result.code) -> log.error("Request failed: $info", result.err ?: noException)
            else                                   -> log.error("Request failed: $info", result.err ?: noException)
        }
    }


    /**
     * Tracks the last job for diagnostics
     */
    fun track(sender: Any, request: Request, result: Response<*>) {
        tracker.requested(request)
        when (result.code) {
            ResultCode.SUCCESS  -> tracker.succeeded(result)
            ResultCode.FILTERED -> tracker.filtered(request)
            ResultCode.FAILURE  -> tracker.failed(request, result.err ?: noException )
            else                -> tracker.failed(request, result.err ?: noException )
        }
    }


    /**
     * Records metrics (counts) each job result
     */
    fun meter(sender: Any, request: Request, result: Response<*>) {
        val tags = listOf("uri", request.path)
        metrics.count("apis.requests.${result.code}", tags)
        metrics.count("apis.total_requests", tags)
        when (result.code) {
            ResultCode.SUCCESS  -> metrics.count("apis.total_successes", tags)
            ResultCode.FILTERED -> metrics.count("apis.total_filtered", tags)
            ResultCode.FAILURE  -> metrics.count("apis.total_failed", tags)
            else                -> metrics.count("apis.total_other", tags)
        }
    }


    /**
     * Events out the job result to potential listeners
     */
    fun notify(sender: Any, request: Request, result: Response<*>) {
        events.onReqest(sender, request)
        when (result.code) {
            ResultCode.SUCCESS  -> events.onSuccess(sender, request, result)
            ResultCode.FILTERED -> events.onFiltered(sender, request, result)
            ResultCode.FAILURE  -> events.onErrored(sender, request, result)
            else                -> events.onEvent(sender, request, result)
        }
    }
}
