package slatekit.meta

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import slatekit.common.Transformer

interface JSONTransformer<S> : Transformer<S, JSONObject, String> {
    /**
     * Encodes the item to a JSON string or null
     */
    override fun encode(data:S?):String? {
        val json= convert(data)
        return json?.toJSONString()
    }

    /**
     * Decodes JSON string to item or null
     */
    override fun decode(item:String?):S? {
        return item?.let {
            val json = JSONParser()
            val jsonItem = json.parse(it)
            when(jsonItem) {
                is JSONObject -> restore(jsonItem)
                else          -> null
            }
        }
    }
}