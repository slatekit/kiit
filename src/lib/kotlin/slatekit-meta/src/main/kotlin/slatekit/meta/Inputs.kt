package slatekit.meta

import slatekit.common.convert.Conversions
import slatekit.common.Inputs
import slatekit.common.crypto.*
import kotlin.reflect.KClass
import kotlin.reflect.KType



/**
 * Load a instance of the class supplied from the Inputs
 */
inline fun <reified T> Inputs.load(prefix:String):T {
    val cls = T::class
    val props = Reflector.getProperties(cls)
    val items = props.map { prop ->
        val key = "$prefix.${prop.name}"
        val converted = this.getValue(key, prop.returnType)
        converted
    }
    val instance = Reflector.createWithArgs<Any>(cls, items.toTypedArray())
    return instance as T
}


fun <T> Inputs.map(prefix: String, cls: KClass<*>, decryptor: ((String) -> String)? = null): T {

    // val cls = dataType.classifier as KClass<*>
    val props = Reflector.getProperties(cls)
    val converted = props.map { prop ->
        val paramType = prop.returnType
        val propKey = "$prefix.${prop.name}"
        val rawVal = get(propKey)

        // Can not handle nulls/default values at the moment.
        rawVal?.let { raw ->
            val result = convert(paramType, raw, decryptor)
            result
        }
    }
    val instance = Reflector.createWithArgs<T>(cls, converted.toTypedArray())
    return instance
}


/**
 * Gets basic data types from the inputs provided
 */
fun Inputs.getValue(key:String, paramType: KType): Any? {
    return when (paramType.classifier) {
        // Basic types
        KTypes.KStringType.classifier        -> this.getStringOrNull(key)
        KTypes.KBoolType.classifier          -> this.getBoolOrNull(key)
        KTypes.KShortType.classifier         -> this.getShortOrNull(key)
        KTypes.KIntType.classifier           -> this.getIntOrNull(key)
        KTypes.KLongType.classifier          -> this.getLongOrNull(key)
        KTypes.KFloatType.classifier         -> this.getFloatOrNull(key)
        KTypes.KDoubleType.classifier        -> this.getDoubleOrNull(key)
        KTypes.KLocalDateType.classifier     -> this.getLocalDateOrNull(key)
        KTypes.KLocalTimeType.classifier     -> this.getLocalTimeOrNull(key)
        KTypes.KLocalDateTimeType.classifier -> this.getLocalDateTimeOrNull(key)
        KTypes.KZonedDateTimeType.classifier -> this.getZonedDateTimeOrNull(key)
        KTypes.KDateTimeType.classifier      -> this.getDateTimeOrNull(key)
        KTypes.KUUIDType.classifier          -> this.getUUIDOrNull(key)
        else                                 -> null
    }
}


fun convert(paramType: KType, rawVal: Any?, decryptor: ((String) -> String)? = null): Any {

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
