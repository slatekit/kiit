package kiit.server.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import kiit.common.types.*
import kiit.requests.Request
import kiit.results.Outcome
import kiit.results.builders.Outcomes
import java.io.InputStream

object KtorMultiParts {

    /**
     * Gets all the data in the multi-part / form-data as @see[kiit.common.types.Content]
     * 1. FormField  -> ContentText
     * 2. FileUpload -> ContentFile
     *
     * E.g. The following will get mapped :
     * Multi-Part {
     *   "uuid" -> "uuid1234"      -> ContentText(data = byteArrayOf(), text = "uuid1234", type = ContentTypes.Plain)
     *   "file" -> File: {BYTES}   -> ContentFile(name = "file1.jpg", data = file.bytes, text = null, type = ContentTypes.Plain)
     * }
     */
    suspend fun loadContentMulti(req: Request): Outcome<ContentMulti> {
        val kreq = req as KtorRequest
        val call = kreq.call
        val multiPart = call.receiveMultipart()
        val fields = mutableMapOf<String, ContentText>()
        val files = mutableMapOf<String, ContentFile>()
        var ndx = 0
        multiPart.forEachPart { part ->
            val name = part.name ?: "field${ndx}"
            if( part is PartData.FormItem) {
                fields[name] = ContentText(part.value.toByteArray(), part.value, ContentTypes.Plain)
            }
            if(part is PartData.FileItem) {
                part.streamProvider().use { stream ->
                    val file = KtorUtils.buildDoc(part, stream)
                    files[name] = file
                }
            }
            ndx += 1
        }
        return Outcomes.success(ContentMulti(fields, files))
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


    suspend fun loadFile(call: ApplicationCall, name:String?, callback:((InputStream)-> ContentFile?)? = null): ContentFile {
        val filePart = loadFilePart(call, name)
        val doc = filePart?.let {
            val file = it
            val doc = file.streamProvider().use{ stream ->
                when(callback) {
                    null -> KtorUtils.buildDoc(file, stream)
                    else -> callback(stream)
                }
            }
            doc
        } ?: ContentFiles.empty
        return doc
    }
}