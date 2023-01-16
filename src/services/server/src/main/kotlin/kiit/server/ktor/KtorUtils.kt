package kiit.server.ktor

import io.ktor.http.HttpMethod
import io.ktor.http.content.PartData
import io.ktor.request.*
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import kiit.common.ext.toId
import kiit.common.types.*
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


    fun buildDoc(part: PartData.FileItem, stream: InputStream):ContentFile {
        val bis = BufferedInputStream(stream)
        val buf = ByteArrayOutputStream()
        var ris = bis.read()
        while (ris != -1) {
            buf.write(ris.toByte().toInt())
            ris = bis.read()
        }
        val defaultName = part.name ?: "file"
        val name = cleanName(part.originalFileName, defaultName)
        val contentType = when {
            part.contentType?.contentType == null -> ContentTypes.Octet
            part.contentType?.contentSubtype == null -> ContentTypes.Octet
            else ->  ContentType(part.contentType?.contentType + "/" + part.contentType?.contentSubtype, part.contentType?.contentSubtype ?: "")
        }
        val doc = ContentFile(name, buf.toByteArray(), null, contentType)
        return doc
    }


    fun isBodyAllowed(method: HttpMethod): Boolean {
        return when(method) {
            HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch, HttpMethod.Delete -> true
            else -> false
        }
    }


    fun cleanName(suppliedName:String?, fileName:String): String {
        val fullname = when {
            suppliedName == null -> fileName
            suppliedName.isNullOrEmpty() -> fileName
            else -> suppliedName
        }
        val info = extractInfo(fullname)
        val name = info.first.toId()
        return name
    }

    /**
     * Remove this in place of some file utilities or Path
     */
    fun extractInfo(fullName:String):Pair<String, String> {
        val ndxDot = fullName.lastIndexOf(".")
        val ext = fullName.substring(ndxDot + 1)
        val name = fullName.substring(0, ndxDot)
        return Pair(name, ext)
    }
}