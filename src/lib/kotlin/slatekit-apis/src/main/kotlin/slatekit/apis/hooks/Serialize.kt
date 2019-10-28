package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.common.Ignore
import slatekit.common.content.Content
import slatekit.common.content.ContentTypeCsv
import slatekit.common.content.ContentTypeJson
import slatekit.common.content.ContentTypeProp
import slatekit.functions.Output
import slatekit.meta.Serialization
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.getOrElse


class Serialize(val serializer: ((String, Any?) -> String)? = null) : Output<ApiRequest, ApiResult> {

    @Ignore
    override suspend fun process(req: ApiRequest, result: Outcome<ApiResult>): Outcome<ApiResult> {
        return if (result.success && !req.request.output.isNullOrEmpty()) {
            val finalSerializer = serializer ?: this::serialize
            val serialized = finalSerializer(req.request.output ?: "", result.getOrElse { null })
            (result as Success).copy(value = serialized!!)
        } else {
            result
        }
    }


    /**
     * Explicitly supplied content
     * Return the value of the result as a content with type
     */
    private fun serialize(format: String, data: Any?): Any? {

        val content = when (format) {
            ContentTypeCsv.ext -> Content.csv(Serialization.csv().serialize(data))
            ContentTypeJson.ext -> Content.json(Serialization.json().serialize(data))
            ContentTypeProp.ext -> Content.prop(Serialization.props(true).serialize(data))
            else -> data
        }
        return content
    }
}
