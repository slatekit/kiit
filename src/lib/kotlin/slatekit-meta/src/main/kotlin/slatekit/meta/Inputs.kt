package slatekit.meta

import slatekit.common.Conversions
import slatekit.common.Inputs
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.encrypt.EncString
import kotlin.reflect.KClass
import kotlin.reflect.KType

fun <T> Inputs.map(key: String, cls: KClass<*>, decryptor: ((String) -> String)? = null): T ? {

    // val cls = dataType.classifier as KClass<*>
    val props = Reflector.getProperties(cls)
    val converted = props.map { prop ->
        val paramType = prop.returnType
        val propKey = "$key.${prop.name}"
        val rawVal = getObject(propKey)

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

fun convert(key: String, paramType: KType, rawVal: Any?, decryptor: ((String) -> String)? = null): Any {

    return when (paramType) {
    // Basic types
        KTypes.KStringType -> Conversions.handleString(rawVal)
        KTypes.KBoolType -> rawVal.toString().toBoolean()
        KTypes.KShortType -> rawVal.toString().toShort()
        KTypes.KIntType -> rawVal.toString().toInt()
        KTypes.KLongType -> rawVal.toString().toLong()
        KTypes.KFloatType -> rawVal.toString().toFloat()
        KTypes.KDoubleType -> rawVal.toString().toDouble()
        KTypes.KLocalDateType -> Conversions.toLocalDate(rawVal as String)
        KTypes.KLocalTimeType -> Conversions.toLocalTime(rawVal as String)
        KTypes.KLocalDateTimeType -> Conversions.toLocalDateTime(rawVal as String)
        KTypes.KZonedDateTimeType -> Conversions.toZonedDateTime(rawVal as String)
        KTypes.KDateTimeType -> Conversions.toDateTime(rawVal as String)
        KTypes.KDecIntType -> decryptor?.let { e -> EncInt(rawVal as String, e(rawVal).toInt()) } ?: EncInt("", 0)
        KTypes.KDecLongType -> decryptor?.let { e -> EncLong(rawVal as String, e(rawVal).toLong()) } ?: EncLong("", 0L)
        KTypes.KDecDoubleType -> decryptor?.let { e -> EncDouble(rawVal as String, e(rawVal).toDouble()) } ?: EncDouble("", 0.0)
        KTypes.KDecStringType -> decryptor?.let { e -> EncString(rawVal as String, e(rawVal)) } ?: EncString("", "")
        KTypes.KDocType -> Conversions.toDoc(rawVal.toString())
        KTypes.KVarsType -> Conversions.toVars(rawVal)

    // Complex types not supported: e.g. Lists/Maps/Nested objects
        else -> Conversions.handleString(rawVal)
    }
}
