package slatekit.meta

import slatekit.common.Conversions
import slatekit.common.Inputs
import slatekit.common.Types
import slatekit.common.encrypt.DecDouble
import slatekit.common.encrypt.DecInt
import slatekit.common.encrypt.DecLong
import slatekit.common.encrypt.DecString
import kotlin.reflect.KClass
import kotlin.reflect.KType


fun <T> Inputs.map(key:String, cls: KClass<*>, decryptor:((String) -> String)? = null): T ? {

    //val cls = dataType.classifier as KClass<*>
    val props = Reflector.getProperties(cls)
    val converted = props.map { prop ->
        val paramType = prop.returnType
        val key = "${key}.${prop.name}"
        val rawVal = getObject(key)

        // Can not handle nulls/default values at the moment.
        rawVal?.let { raw ->
            val result = convert("", paramType, raw, decryptor)
            result
        }
    }
    val filtered = converted.filterNotNull()
    val instance = Reflector.createWithArgs<T>(cls, filtered.toTypedArray())
    return instance
}


fun convert(key:String, paramType: KType, rawVal:Any?, decryptor:((String) -> String)? = null): Any {

    val result = when (paramType) {
    // Basic types
        KTypes.KStringType       -> Conversions.handleString(rawVal)
        KTypes.KBoolType          -> rawVal.toString().toBoolean()
        KTypes.KShortType         -> rawVal.toString().toShort()
        KTypes.KIntType           -> rawVal.toString().toInt()
        KTypes.KLongType          -> rawVal.toString().toLong()
        KTypes.KFloatType         -> rawVal.toString().toFloat()
        KTypes.KDoubleType        -> rawVal.toString().toDouble()
        KTypes.KLocalDateType     -> Conversions.toLocalDate(rawVal as String)
        KTypes.KLocalTimeType     -> Conversions.toLocalTime(rawVal as String)
        KTypes.KLocalDateTimeType -> Conversions.toLocalDateTime(rawVal as String)
        KTypes.KZonedDateTimeType -> Conversions.toZonedDateTime(rawVal as String)
        KTypes.KDateTimeType      -> Conversions.toDateTime(rawVal as String)
        KTypes.KDecIntType        -> decryptor?.let { e -> DecInt(e(rawVal as String).toInt()) } ?: DecInt(0)
        KTypes.KDecLongType       -> decryptor?.let { e -> DecLong(e(rawVal as String).toLong()) } ?: DecLong(0L)
        KTypes.KDecDoubleType     -> decryptor?.let { e -> DecDouble(e(rawVal as String).toDouble()) } ?: DecDouble(0.0)
        KTypes.KDecStringType     -> decryptor?.let { e -> DecString(e(rawVal as String)) } ?: DecString("")
        KTypes.KDocType           -> Conversions.toDoc(rawVal.toString())
        KTypes.KVarsType          -> Conversions.toVars(rawVal)

    // Complex types not supported: e.g. Lists/Maps/Nested objects
        else                  -> Conversions.handleString(rawVal)
    }
    return result
}