package slatekit.meta.models

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import slatekit.common.DateTime
import slatekit.meta.KTypes
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

object ModelUtils {

    fun fieldType(field:KProperty<*>):FieldType {
        val fieldCls = KTypes.getClassFromType(field.returnType)
        return fieldType(fieldCls)
    }

    fun fieldType(fieldCls:KClass<*>):FieldType {
        val fieldType = when(fieldCls){
            Boolean::class -> FieldType.typeBool
            Char::class -> FieldType.typeChar
            String::class -> FieldType.typeString
            Short::class -> FieldType.typeShort
            Int::class -> FieldType.typeInt
            Long::class -> FieldType.typeLong
            Float::class -> FieldType.typeFloat
            Double::class -> FieldType.typeDouble
            LocalDate::class -> FieldType.typeLocalDate
            LocalTime::class -> FieldType.typeLocalTime
            LocalDateTime::class -> FieldType.typeLocalDateTime
            ZonedDateTime::class -> FieldType.typeZonedDateTime
            DateTime::class -> FieldType.typeDateTime
            Enum::class -> FieldType.typeEnum
            UUID::class -> FieldType.typeUUID
            else -> FieldType.typeObject
        }
        return fieldType
    }


    fun getFieldType(tpe: KType): FieldType {
        return when (tpe) {
            // Basic types
            KTypes.KStringType -> FieldType.typeString
            KTypes.KBoolType -> FieldType.typeBool
            KTypes.KShortType -> FieldType.typeShort
            KTypes.KIntType -> FieldType.typeInt
            KTypes.KLongType -> FieldType.typeLong
            KTypes.KFloatType -> FieldType.typeFloat
            KTypes.KDoubleType -> FieldType.typeDouble
            KTypes.KDateTimeType -> FieldType.typeDateTime
            KTypes.KLocalDateType -> FieldType.typeLocalDate
            KTypes.KLocalTimeType -> FieldType.typeLocalTime
            KTypes.KLocalDateTimeType -> FieldType.typeLocalDateTime
            KTypes.KZonedDateTimeType -> FieldType.typeZonedDateTime
            KTypes.KInstantType -> FieldType.typeInstant
            KTypes.KUUIDType -> FieldType.typeUUID
            KTypes.KUPIDType -> FieldType.typeUnique
            else               -> FieldType.typeObject
        }
    }
}