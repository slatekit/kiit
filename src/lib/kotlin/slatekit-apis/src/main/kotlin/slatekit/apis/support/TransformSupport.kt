package slatekit.apis.support

import org.json.simple.JSONObject
import slatekit.apis.ApiRequest
import slatekit.meta.JSONTransformable

interface TransformSupport<S> : JSONTransformable<S> {

    val isConvertable: Boolean

    val isRestorable: Boolean

    fun convert(req: ApiRequest, data:S?): JSONObject? {
        return convert(data)
    }

    fun restore(req: ApiRequest, target: JSONObject?, name:String):S? {
        return restore(target)
    }

    fun encode(req: ApiRequest, data:S?):String? {
        return encode(data)
    }

    fun decode(req: ApiRequest, item:String?):S? {
        return decode(item)
    }
}
