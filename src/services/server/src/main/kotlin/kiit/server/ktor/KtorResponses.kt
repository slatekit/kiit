package kiit.server.ktor

import io.ktor.server.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondBytes
import kiit.common.types.ContentData
import kiit.common.types.ContentTypes
import kiit.results.*

object KtorResponses {

    fun status(status: Status): HttpStatusCode {
        val info = Codes.toHttp(status)
        val code = info.first
        val desc = info.second.desc
        return HttpStatusCode(code, desc)
    }

    suspend fun respond(call: ApplicationCall, result:Outcome<ContentData>) {
        return when(result) {
            is Success -> {
                val contentType = io.ktor.http.ContentType.parse(result.value.tpe.http)
                val status = status(result.status)
                call.respondBytes(result.value.data, contentType, status)
            }
            is Failure -> {
                val status = status(result.status)
                call.respondBytes(byteArrayOf(), io.ktor.http.ContentType.parse(ContentTypes.Json.http), status)
            }
        }
    }
}