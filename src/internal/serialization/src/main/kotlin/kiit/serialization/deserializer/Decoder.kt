package kiit.serialization.deserializer

import kiit.meta.KTypes
import kotlin.reflect.KType


typealias DecodeFunction = (Any, String, Any?, KType) -> Unit


/**
 * Interface for any decoder from source A to destination B
 * For example, a JSON decoder that takes a JSON document and converts a key/value pair to boolean.
 */
interface Decoder<T> {
    /**
     * Given following Source JSON
     *
     * {
     *       "userId"  : "user123",
     *       "account" : 1234567  ,
     *       "isActive": true
     * }
     *
     * @param context   : Context info                    : e.g. could be a HttpRequest object
     * @param parent    : Parent object of current item   : e.g. if "account", this is JSON Root Obj above
     * @param paramName : Parameter name of  current item : e.g. if "account", this is "account"
     * @param paramValue: Parameter value of current item : e.g. if "account", this is value 1234567
     * @param paramType : Parameter type of current item  : e.g. if "account", this is Int
     */
    fun decode(context:Any, parent: T, paramName: String, paramValue: Any?, paramType: KType): Any?
}


interface DecodeSupport {

    fun handle(raw:Any?, nullValue:Any?, elseValue:() -> Any?):Any? {
        return when (raw) {
            null   -> nullValue
            else   -> elseValue()
        }
    }
}


object DecodeUtils {
    fun basicMapTypes() : Map<String, Boolean> {
        return mapOf(
            KTypes.KStringClass.qualifiedName!! to true,
            KTypes.KBoolClass.qualifiedName!! to true,
            KTypes.KShortClass.qualifiedName!! to true,
            KTypes.KIntClass.qualifiedName!! to true,
            KTypes.KLongClass.qualifiedName!! to true,
            KTypes.KFloatClass.qualifiedName!! to true,
            KTypes.KDoubleClass.qualifiedName!! to true,
            KTypes.KLocalDateClass.qualifiedName!! to true,
            KTypes.KLocalTimeClass.qualifiedName!! to true,
            KTypes.KLocalDateTimeClass.qualifiedName!! to true,
            KTypes.KZonedDateTimeClass.qualifiedName!! to true,
            KTypes.KDateTimeClass.qualifiedName!! to true,
            KTypes.KDecIntClass.qualifiedName!! to true,
            KTypes.KDecLongClass.qualifiedName!! to true,
            KTypes.KDecDoubleClass.qualifiedName!! to true,
            KTypes.KDecStringClass.qualifiedName!! to true,
            KTypes.KVarsClass.qualifiedName!! to true,
            KTypes.KDocClass.qualifiedName!! to true,
            KTypes.KUUIDClass.qualifiedName!! to true
        )
    }
}