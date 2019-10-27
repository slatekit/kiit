package slatekit.apis.helpers

import slatekit.apis.ApiResult
import slatekit.apis.ApiServer
import slatekit.common.*
import slatekit.common.content.Content
import slatekit.common.content.ContentTypeCsv
import slatekit.common.content.ContentTypeJson
import slatekit.common.content.ContentTypeProp
import slatekit.common.requests.Request
import slatekit.meta.Serialization
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.getOrElse


class ApiResults(val ctx: Context,
                 val host:ApiServer,
                 val serializer: ((String, Any?) -> String)? = null) {

    private val emptyArgs = mapOf<String, Any>()


    /**
     * Finally: If the format of the content specified ( json | csv | props )
     * Then serialize it here and return the content
     */
    fun convert(req: Request, result: Outcome<ApiResult>): Outcome<ApiResult> {
        return if (result.success && !req.output.isNullOrEmpty()) {
            val finalSerializer = serializer ?: this::serialize
            val serialized = finalSerializer(req.output ?: "", result.getOrElse { null })
            (result as Success).copy(value = serialized!!)
        } else {
            result
        }
    }


    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    fun serialize(format: String, data: Any?): Any? {

        val content = when (format) {
            ContentTypeCsv.ext -> Content.csv(Serialization.csv().serialize(data))
            ContentTypeJson.ext -> Content.json(Serialization.json().serialize(data))
            ContentTypeProp.ext -> Content.prop(Serialization.props(true).serialize(data))
            else -> data
        }
        return content
    }
}