package kiit.server.ktor

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.uri
import kiit.requests.Request

object KtorRequests {
    /**
     * Gets all the parts of the uri path
     * @sample "/api/account/signup"
     * @return ["api", "account", "signup"]
     */
    fun getPathParts(req: Request): List<String> {
        val kreq = req as KtorRequest?
        return kreq?.call?.let { getPathParts(it) } ?: listOf()
    }


    /**
     * Gets all the parts of the uri path
     * @sample "/api/account/signup"
     * @return ["api", "account", "signup"]
     */
    fun getPathParts(call: ApplicationCall): List<String> {
        val rawUri = call.request.uri
        // E.g. app/users/recent?count=20
        // Only get up until "?"
        val uri = if (rawUri.contains("?")) {
            rawUri.substring(0, rawUri.indexOf("?"))
        } else {
            rawUri
        }
        val parts = uri.split('/')
        return parts
    }
}