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

    fun fieldType(field:KProperty<*>):ModelFieldType {
        val fieldCls = KTypes.getClassFromType(field.returnType)
        return fieldType(fieldCls)
    }

    fun fieldType(fieldCls:KClass<*>):ModelFieldType {
        val fieldType = when(fieldCls){
            Boolean::class -> ModelFieldType.typeBool
            Char::class -> ModelFieldType.typeChar
            String::class -> ModelFieldType.typeString
            Short::class -> ModelFieldType.typeShort
            Int::class -> ModelFieldType.typeInt
            Long::class -> ModelFieldType.typeLong
            Float::class -> ModelFieldType.typeFloat
            Double::class -> ModelFieldType.typeDouble
            LocalDate::class -> ModelFieldType.typeLocalDate
            LocalTime::class -> ModelFieldType.typeLocalTime
            LocalDateTime::class -> ModelFieldType.typeLocalDateTime
            ZonedDateTime::class -> ModelFieldType.typeZonedDateTime
            DateTime::class -> ModelFieldType.typeDateTime
            Enum::class -> ModelFieldType.typeEnum
            UUID::class -> ModelFieldType.typeUUID
            else -> ModelFieldType.typeObject
        }
        return fieldType
    }


    fun getFieldType(tpe: KType): ModelFieldType {
        return when (tpe) {
            // Basic types
            KTypes.KStringType -> ModelFieldType.typeString
            KTypes.KBoolType -> ModelFieldType.typeBool
            KTypes.KShortType -> ModelFieldType.typeShort
            KTypes.KIntType -> ModelFieldType.typeInt
            KTypes.KLongType -> ModelFieldType.typeLong
            KTypes.KFloatType -> ModelFieldType.typeFloat
            KTypes.KDoubleType -> ModelFieldType.typeDouble
            KTypes.KDateTimeType -> ModelFieldType.typeDateTime
            KTypes.KLocalDateType -> ModelFieldType.typeLocalDate
            KTypes.KLocalTimeType -> ModelFieldType.typeLocalTime
            KTypes.KLocalDateTimeType -> ModelFieldType.typeLocalDateTime
            KTypes.KZonedDateTimeType -> ModelFieldType.typeZonedDateTime
            KTypes.KInstantType -> ModelFieldType.typeInstant
            KTypes.KUUIDType -> ModelFieldType.typeUUID
            KTypes.KUniqueIdType -> ModelFieldType.typeUnique
            else               -> ModelFieldType.typeObject
        }
    }
}