package slatekit.server.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.ApplicationRequest
import io.ktor.request.httpMethod
import io.ktor.request.isMultipart
import io.ktor.request.receiveMultipart
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.common.types.ContentTypeHtml
import slatekit.common.types.ContentTypeText
import slatekit.common.types.Doc
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

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


    suspend fun loadFile(call:ApplicationCall, callback:((InputStream)-> Doc?)? = null):Doc {
        val multiPart = call.receiveMultipart()
        //val parts = multiPart.readAllParts()
        //val part = parts.find { (it.name ?: "") == name }
        var filePart: PartData.FileItem? = null
        multiPart.forEachPart {
            when(it) {
                is PartData.FileItem -> {
                    if(filePart == null) {
                        filePart = it
                    }
                }
            }
        }
        val doc = filePart?.let {
            val file = it
            val doc = file.streamProvider().use{ stream ->
                when(callback) {
                    null -> loadFile( file.originalFileName ?: "", stream)
                    else -> callback(stream)
                }
            }
            doc
        } ?: Doc.empty
        return doc
    }


    fun loadFile(name:String, stream:InputStream):Doc {
        val bis = BufferedInputStream(stream)
        val buf = ByteArrayOutputStream()
        var ris = bis.read()
        while (ris != -1) {
            buf.write(ris.toByte().toInt())
            ris = bis.read()
        }
        val text = buf.toString()
        val doc = Doc(name, text, ContentTypeText, text.length.toLong())
        return doc
    }


    fun isBodyAllowed(method: HttpMethod): Boolean {
        return when(method) {
            HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete -> true
            else -> false
        }
    }
}