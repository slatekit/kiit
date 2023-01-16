package kiit.server.core

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import kiit.common.types.Content
import kiit.common.types.ContentData
import kiit.common.types.ContentFile
import kiit.common.types.ContentText
import kiit.requests.Response

interface ResponseHandler {
    /**
     * Returns the value of the result as an html(string)
     */
    suspend fun result(call: ApplicationCall, result: Response<Any?>): Any

    /**
     * Returns the value of the resulut as JSON.
     */
    suspend fun json(call: ApplicationCall, result: Response<Any?>)

    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    suspend fun contentText(call: ApplicationCall, result: Response<Any?>, content: ContentText)

    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    suspend fun contentData(call: ApplicationCall, result: Response<Any?>, content: ContentData)

    /**
     * Returns the value of the result as a file document
     */
    suspend fun contentFile(call: ApplicationCall, result: Response<Any?>, doc: ContentFile)


    fun toHttpStatus(response: Response<Any?>): HttpStatusCode
}