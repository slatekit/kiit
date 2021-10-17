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
import slatekit.common.types.ContentTypeText
import slatekit.common.types.ContentFile
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


    suspend fun loadPart(call: ApplicationCall, filter:(PartData) -> Boolean ) : PartData? {
        val multiPart = call.receiveMultipart()
        //val parts = multiPart.readAllParts()
        //val part = parts.find { (it.name ?: "") == name }
        var part: PartData? = null
        multiPart.forEachPart {
            if(filter(it)) {
                if(part == null) {
                    part = it
                }
            }
        }
        return part
    }


    suspend fun loadFilePart(call: ApplicationCall, name:String?) : PartData.FileItem? {
        val filePart = loadPart(call) {
            when(it is PartData.FileItem){
                true -> {
                    val file = it
                    if(name == null) true else file.originalFileName == name
                }
                false -> false
            }
        } as PartData.FileItem?
        return filePart
    }


    suspend fun loadFileStream(call: ApplicationCall, name:String?) : InputStream? {
        val filePart = loadFilePart(call, name)
        return filePart?.streamProvider?.invoke()
    }


    suspend fun loadFile(call:ApplicationCall, name:String?, callback:((InputStream)-> ContentFile?)? = null):ContentFile {
        val filePart = loadFilePart(call, name)
        val doc = filePart?.let {
            val file = it
            val doc = file.streamProvider().use{ stream ->
                when(callback) {
                    null -> buildDoc( file.originalFileName ?: "", stream)
                    else -> callback(stream)
                }
            }
            doc
        } ?: ContentFile.empty
        return doc
    }


    fun buildDoc(name:String, stream:InputStream):ContentFile {
        val bis = BufferedInputStream(stream)
        val buf = ByteArrayOutputStream()
        var ris = bis.read()
        while (ris != -1) {
            buf.write(ris.toByte().toInt())
            ris = bis.read()
        }
        val text = buf.toString()
        val doc = ContentFile(name, buf.toByteArray(), text, ContentTypeText, text.length.toLong())
        return doc
    }


    fun isBodyAllowed(method: HttpMethod): Boolean {
        return when(method) {
            HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete -> true
            else -> false
        }
    }
}