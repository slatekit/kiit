package slatekit.meta.deserializer

import slatekit.common.crypto.Encryptor
import slatekit.meta.Conversion
import kotlin.reflect.KType

interface DeserializerPart {
    val conversion: Conversion
    val enc: Encryptor?

    /**
     * Given following Source JSON
     * {
     *       "userId"  : "user123",
     *       "account" : 1234567  ,
     *       "isActive": true
     * }
     * @param context   : Context info                    : e.g. could be a HttpRequest object
     * @param parent    : Parent object of current item   : e.g. if "account", this is JSON Root Obj
     * @param paramName : Parameter name of  current item : e.g. if "account", this is "account"
     * @param paramValue: Parameter value of current item : e.g. if "account", this is value 1234567
     * @param paramType : Parameter type of current item  : e.g. if "account", this is Int
     */
    fun deserialize(context:Any, parent:Any, paramValue:Any?, paramName:String, paramType: KType):Any?
}