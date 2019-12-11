package slatekit.server.ktor

import io.ktor.http.HttpMethod
import io.ktor.request.ApplicationRequest
import io.ktor.request.httpMethod
import io.ktor.request.isMultipart
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

object KtorUtils {
    /**
     * Load json from the post/put body using json-simple
     */
    fun loadJson(body: String, req: ApplicationRequest, addQueryParams: Boolean = false): JSONObject {
        val isMultiPart = req.isMultipart()
        val isBodyAllowed = isBodyAllowed(req.httpMethod)
        val json = if (isBodyAllowed && !isMultiPart && !body.isNullOrEmpty()) {
            val parser = JSONParser()
            val root = parser.parse(body)
            root as JSONObject

            // Add query params
            if (addQueryParams && !req.queryParameters.isEmpty()) {
                req.queryParameters.names().forEach { key ->
                    root.put(key, req.queryParameters.get(key))
                }
            }
            root
        } else {
            JSONObject()
        }
        return json
    }

    fun isBodyAllowed(method: HttpMethod): Boolean {
        return when(method) {
            HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete -> true
            else -> false
        }
    }
}