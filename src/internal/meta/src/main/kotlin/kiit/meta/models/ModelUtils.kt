package kiit.meta.models

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import kiit.common.DateTime
import kiit.common.data.DataType
import kiit.common.ids.UPID
import kiit.meta.KTypes
import kiit.meta.Reflector
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

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
            UPID::class          -> DataType.DTUPID
            else                 -> {
                when {
                    Reflector.isSlateKitEnum(fieldCls) -> {
                        DataType.DTEnum
                    }
                    fieldCls.isSubclassOf(Enum::class) -> {
                        DataType.DTEnum
                    }
                    else -> {
                        DataType.DTObject
                    }
                }
            }
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