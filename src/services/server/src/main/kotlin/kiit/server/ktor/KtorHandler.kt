package kiit.server.ktor

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.request.httpMethod
import io.ktor.request.isMultipart
import io.ktor.request.receiveText
import io.ktor.routing.*
import kiit.apis.ApiServer
import kiit.context.Context
import kiit.telemetry.Diagnostics
import kiit.requests.Request
import kiit.requests.toResponse
import kiit.server.ServerSettings
import kiit.server.core.RequestHandler
import kiit.server.core.ResponseHandler
import kiit.server.core.ServerDiagnostics
import kiit.telemetry.MetricsLite


class KtorHandler(
        override val context: Context,
        val settings: ServerSettings,
        override val container:ApiServer,
        override val diagnostics: Diagnostics<Request> = diagnostics(context),
        override val responses: ResponseHandler = KtorResponse(settings),
) : RequestHandler {

    private fun path(prefix: String, path:String, version:String?) : String {
        val finalPrefix = if(prefix.endsWith("/")) prefix else "${prefix}/"
        val fullPath = when (version) {
            null -> "${finalPrefix}${path}"
            else -> "${finalPrefix}${version}/${path}"
        }
        println("full path = $fullPath")
        return fullPath
    }


    override fun register(routes:Routing){
        val explicitVersions = container.routes.map { it.version }.distinct()
        // This is for backwards compatibility /{area}/{api}/{action} ( without version )
        // This defaults version to "version" = 0
        val implicitVersion = listOf<String?>(null)
        val versions = implicitVersion + explicitVersions
        versions.forEach { version ->
            routes.get(path(settings.prefix, "help", version)) {
                exec(call)
            }
            routes.get(path(settings.prefix, "*/help", version)) {
                exec(call)
            }
            routes.get(path(settings.prefix, "*/*/help", version)) {
                exec(call)
            }
            routes.get(path(settings.prefix, "*/*/*/help", version)) {
                exec(call)
            }
            routes.get(path(settings.prefix, "*/*/*", version)) {
                exec(call)
            }
            routes.post(path(settings.prefix, "*/*/*", version)) {
                exec(call)
            }
            routes.put(path(settings.prefix, "*/*/*", version)) {
                exec(call)
            }
            routes.patch(path(settings.prefix, "*/*/*", version)) {
                exec(call)
            }
            routes.delete(path(settings.prefix, "*/*/*", version)) {
                exec(call)
            }
        }
    }

    /**
     * handles the core logic of execute the http request.
     * This is actually accomplished by the SlateKit API Container
     * which handles abstracted Requests and dispatches them to
     * Slate Kit "Protocol Independent APIs".
     */
    override suspend fun exec(call: ApplicationCall) {

        // see: https://github.com/ktorio/ktor/issues/482
        // If multi-part / file upload, we can not call both receiveMultiPart ( later )
        // and receiveText
        val body = if(!call.request.isMultipart()) {
            when (call.request.httpMethod) {
                HttpMethod.Post -> call.receiveText()
                HttpMethod.Put -> call.receiveText()
                HttpMethod.Patch -> call.receiveText()
                HttpMethod.Delete -> call.receiveText()
                else -> ""
            }
        } else {
            ""
        }


        // Convert the http request to a SlateKit Request
        val request = KtorRequest.build(context, body, call, settings)

        // Execute the API call
        // The SlateKit ApiServer will handle the heavy work of
        // 1. Checking routes to area/api/actions ( methods )
        // 2. Validating parameters to methods
        // 3. Decoding request to method parameters
        // 4. Executing the method
        // 5. Handling errors
        val result = container.executeAttempt(request)
        val response = result.toResponse()

        // Record all diagnostics
        // e.g. logs, track, metrics, event
        diagnostics.record(container, request, response, null)

        // Finally convert the result back to a HttpResult
        responses.result(call, response)
    }


    //suspend fun testFile(call:ApplicationCall){
    //    val doc = KtorUtils.loadFile(call, null)
    //    println(doc)
    //}


    companion object {
        fun diagnostics(ctx: Context):Diagnostics<Request> {
            val metrics = MetricsLite(ctx.info.about.toId())
            val diagnostics = ServerDiagnostics("app", ctx.logs.getLogger("app"), metrics, listOf())
            return diagnostics
        }
    }
}