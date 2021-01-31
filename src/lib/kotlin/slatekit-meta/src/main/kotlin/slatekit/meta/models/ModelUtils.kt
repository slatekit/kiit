package slatekit.meta.models

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import slatekit.common.DateTime
import slatekit.common.data.DataType
import slatekit.meta.KTypes
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

object ModelUtils {

    fun fieldType(field:KProperty<*>):DataType {
        val fieldCls = KTypes.getClassFromType(field.returnType)
        return fieldType(fieldCls)
    }

    fun fieldType(fieldCls:KClass<*>):DataType {
        val fieldType = when(fieldCls){
            Boolean::class       -> DataType.DTBool
            Char::class          -> DataType.DTChar
            String::class        -> DataType.DTString
            Short::class         -> DataType.DTShort
            Int::class           -> DataType.DTInt
            Long::class          -> DataType.DTLong
            Float::class         -> DataType.DTFloat
            Double::class        -> DataType.DTDouble
            LocalDate::class     -> DataType.DTLocalDate
            LocalTime::class     -> DataType.DTLocalTime
            LocalDateTime::class -> DataType.DTLocalDateTime
            ZonedDateTime::class -> DataType.DTZonedDateTime
            DateTime::class      -> DataType.DTDateTime
            Enum::class          -> DataType.DTEnum
            UUID::class          -> DataType.DTUUID
            else                 -> DataType.DTObject
        }
        return fieldType
    }


    fun getFieldType(tpe: KType): DataType {
        return when (tpe) {
            // Basic types
            KTypes.KStringType        -> DataType.DTString
            KTypes.KBoolType          -> DataType.DTBool
            KTypes.KShortType         -> DataType.DTShort
            KTypes.KIntType           -> DataType.DTInt
            KTypes.KLongType          -> DataType.DTLong
            KTypes.KFloatType         -> DataType.DTFloat
            KTypes.KDoubleType        -> DataType.DTDouble
            KTypes.KDateTimeType      -> DataType.DTDateTime
            KTypes.KLocalDateType     -> DataType.DTLocalDate
            KTypes.KLocalTimeType     -> DataType.DTLocalTime
            KTypes.KLocalDateTimeType -> DataType.DTLocalDateTime
            KTypes.KZonedDateTimeType -> DataType.DTZonedDateTime
            KTypes.KInstantType       -> DataType.DTInstant
            KTypes.KUUIDType          -> DataType.DTUUID
            KTypes.KULIDType          -> DataType.DTULID
            KTypes.KUPIDType          -> DataType.DTULID
            else                      -> DataType.DTObject
        }
    }
}