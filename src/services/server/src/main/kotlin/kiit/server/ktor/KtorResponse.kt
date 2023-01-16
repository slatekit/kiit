/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.server.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import kiit.common.types.*
import kiit.serialization.responses.ResponseEncoder
import kiit.requests.Response
import kiit.serialization.Serialization
import kiit.results.*
import kiit.server.ServerSettings
import kiit.server.core.ResponseHandler


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
                    is ContentText -> contentText(call, result, result.value as ContentText)
                    is ContentData -> contentData(call, result, result.value as ContentData)
                    is ContentFile -> contentFile(call, result, result.value as ContentFile)
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
    override suspend fun contentText(call: ApplicationCall, result: Response<Any?>, content: ContentText) {
        val text = Contents.toText(content)
        val statusCode = toHttpStatus(result)
        val contentType = ContentType.parse(content.tpe.http)
        call.respondText(text ?: "", contentType, statusCode)
    }

    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    override suspend fun contentData(call: ApplicationCall, result: Response<Any?>, content: ContentData) {
        val statusCode = toHttpStatus(result)
        val contentType = ContentType.parse(content.tpe.http)
        call.respondBytes(content.data, contentType, statusCode)
    }

    /**
     * Returns the value of the result as a file document
     */
    override suspend fun contentFile(call: ApplicationCall, result: Response<Any?>, content: ContentFile) {
        // Make files downloadable
        val statusCode = toHttpStatus(result)
        val contentType = ContentType.parse(content.tpe.http)
        call.response.header("Content-Disposition", "attachment; filename=" + content.name)
        call.respondBytes(content.data, contentType, statusCode)
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
