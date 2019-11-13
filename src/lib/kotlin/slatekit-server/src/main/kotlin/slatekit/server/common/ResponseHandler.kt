package slatekit.server.common

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import slatekit.common.types.Content
import slatekit.common.types.Doc
import slatekit.common.requests.Response

interface ResponseHandler {
    /**
     * Returns the value of the result as an html(string)
     */
    suspend fun result(call: ApplicationCall, result: Response<Any>): Any

    /**
     * Returns the value of the resulut as JSON.
     */
    suspend fun json(call: ApplicationCall, result: Response<Any>)

    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    suspend fun content(call: ApplicationCall, result: Response<Any>, content: Content?)

    /**
     * Returns the value of the result as a file document
     */
    suspend fun file(call: ApplicationCall, result: Response<Any>, doc: Doc)

    fun toHttpStatus(response: Response<Any>): HttpStatusCode
}