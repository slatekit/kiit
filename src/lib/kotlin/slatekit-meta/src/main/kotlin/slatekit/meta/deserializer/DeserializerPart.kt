package slatekit.meta.deserializer

import slatekit.meta.Conversion
import kotlin.reflect.KType

interface DeserializerPart {
    val conversion: Conversion
    fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any?
}