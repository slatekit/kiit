package slatekit.apis.hooks

import slatekit.apis.ApiRequest
import slatekit.apis.ApiResult
import slatekit.common.Ignore
import slatekit.common.types.Content
import slatekit.common.types.ContentTypeCsv
import slatekit.common.types.ContentTypeJson
import slatekit.common.types.ContentTypeProp
import slatekit.policy.Output
import slatekit.meta.Serialization
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success
import slatekit.results.getOrElse

class Serialize(val serializer: ((String, Any?) -> String)? = null) : Output<ApiRequest, ApiResult> {

    @Ignore
    override suspend fun process(raw:ApiRequest, req: Outcome<ApiRequest>, result: Outcome<ApiResult>): Outcome<ApiResult> {
        return when(req) {
            is Failure -> result
            is Success -> {
                when(result) {
                    is Failure -> result
                    is Success -> {
                        if (result.success && !req.value.request.output.isNullOrEmpty()) {
                            val finalSerializer = serializer ?: this::serialize
                            val serialized = finalSerializer(req.value.request.output ?: "", result.getOrElse { null })
                            (result).copy(value = serialized!!)
                        } else {
                            result
                        }
                    }
                }
            }
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
