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

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import slatekit.serialization.responses.ResponseEncoder
import slatekit.common.types.Content
import slatekit.common.types.Doc
import slatekit.requests.Response
import slatekit.serialization.Serialization
import slatekit.results.*
import slatekit.server.ServerSettings
import slatekit.server.common.ResponseHandler


class KtorResponse(val settings:ServerSettings)  : ResponseHandler {

    /**
     * Returns the value of the result as an html(string)
     */
    override suspend fun result(call: ApplicationCall, result: Response<Any?>): Any {
        return when(result.success) {
            false -> {
                when(result.err){
                    is ExceptionErr ->  error(call, result, (result.err as ExceptionErr).err)
                    else ->  json(call, result)
                }
            }
            true -> {
                when (result.value) {
                    is Content -> content(call, result, result.value as Content)
                    is Doc -> file(call, result, result.value as Doc)
                    else -> json(call, result)
                }
            }
        }
    }

    /**
     * Returns the value of the resulut as JSON.
     */
    override suspend fun json(call: ApplicationCall, result: Response<Any?>) {
        val rawJson = Serialization.json(true).serialize(result)
        val json = when(settings.formatJson) {
            false -> rawJson
            true  -> org.json.JSONObject(rawJson).toString(4)
        }
        val contentType = io.ktor.http.ContentType.Application.Json // "application/json"
        val statusCode = toHttpStatus(result)
        call.respondText(json, contentType, statusCode)
    }

    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    override suspend fun content(call: ApplicationCall, result: Response<Any?>, content: Content?) {
        val text = content?.text ?: ""
        val contentType = content?.let { ContentType.parse(it.tpe.http) } ?: io.ktor.http.ContentType.Text.Plain
        val statusCode = toHttpStatus(result)
        call.respondText(text, contentType, statusCode)
    }

    /**
     * Returns the value of the result as a file document
     */
    override suspend fun file(call: ApplicationCall, result: Response<Any?>, doc: Doc) {
        val bytes = doc.content.toByteArray()
        val statusCode = toHttpStatus(result)

        // Make files downloadable
        call.response.header("Content-Disposition", "attachment; filename=" + doc.name)
        call.respondBytes(bytes, ContentType.parse(doc.tpe.http), statusCode)
    }


    override fun toHttpStatus(response:Response<Any?>): HttpStatusCode {
        val http = Codes.toHttp(Passed.Succeeded(response.name, response.code, response.desc ?: ""))
        return HttpStatusCode(http.first, http.second.desc)
    }


    private suspend fun error(call: ApplicationCall, result: Response<Any?>, err: Err){
        val json = ResponseEncoder.response(err, result)
        val text = json.toJSONString()
        val contentType = io.ktor.http.ContentType.Application.Json // "application/json"
        val statusCode = toHttpStatus(result)
        call.respondText(text, contentType, statusCode)
    }
}
