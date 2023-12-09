package kiit.serialization.deserializer.json

import kiit.common.crypto.Encryptor
import kiit.meta.JSONTransformer


class JsonDecoders(val converter: JsonConverter, enc: Encryptor?, decoders:Map<String, JSONTransformer<*>>) {
    val basic  = JsonBasicDecoder(converter, enc)
    val lists  = JsonListDecoder(converter, enc)
    val maps   = JsonMapDecoder(converter, enc)
    val enums  = JsonEnumDecoder(converter, enc)
    val objs   = JsonObjectDecoder(converter, enc)
    val smart  = JsonSmartValueDecoder(converter, enc)
    val custom = JsonCustomDecoder(converter, enc, decoders)
}