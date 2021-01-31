package slatekit.meta

import slatekit.common.*
import slatekit.common.crypto.EncDouble
import slatekit.common.crypto.EncInt
import slatekit.common.crypto.EncLong
import slatekit.common.crypto.EncString
import slatekit.common.ids.UPID
import slatekit.common.types.Content
import slatekit.common.types.Doc
//import java.time.*
import org.threeten.bp.*
import slatekit.common.ext.toStringNumeric
import slatekit.common.ext.toStringTime
import slatekit.common.ext.toStringYYYYMMDD
import slatekit.common.ids.ULID
import slatekit.common.smartvalues.SmartValue
import slatekit.common.smartvalues.SmartValued
import slatekit.common.types.Vars
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object KTypes {

    val KStringClass = String::class
    val KBoolClass = Boolean::class
    val KShortClass = Short::class
    val KIntClass = Int::class
    val KLongClass = Long::class
    val KFloatClass = Float::class
    val KDoubleClass = Double::class
    val KDateTimeClass = DateTime::class
    val KLocalDateClass = LocalDate::class
    val KLocalTimeClass = LocalTime::class
    val KLocalDateTimeClass = LocalDateTime::class
    val KZonedDateTimeClass = ZonedDateTime::class
    val KInstantClass = Instant::class
    val KUUIDClass = java.util.UUID::class
    val KUPIDClass = UPID::class
    val KDocClass = Doc::class
    val KVarsClass = Vars::class
    val KSmartValueClass = SmartValue::class
    val KEnumLikeClass = EnumLike::class
    val KContentClass = Content::class
    val KDecStringClass = EncString::class
    val KDecIntClass = EncInt::class
    val KDecLongClass = EncLong::class
    val KDecDoubleClass = EncDouble::class
    val KAnyClass = EncDouble::class

    val KStringType = String::class.createType()
    val KBoolType = Boolean::class.createType()
    val KShortType = Short::class.createType()
    val KIntType = Int::class.createType()
    val KLongType = Long::class.createType()
    val KFloatType = Float::class.createType()
    val KDoubleType = Double::class.createType()
    val KDateTimeType = DateTime::class.createType()
    val KLocalDateType = LocalDate::class.createType()
    val KLocalTimeType = LocalTime::class.createType()
    val KLocalDateTimeType = LocalDateTime::class.createType()
    val KZonedDateTimeType = ZonedDateTime::class.createType()
    val KInstantType = Instant::class.createType()
    val KUUIDType = java.util.UUID::class.createType()
    val KULIDType = ULID::class.createType()
    val KUPIDType = UPID::class.createType()
    val KDocType = Doc::class.createType()
    val KVarsType = Vars::class.createType()
    val KSmartValueType = SmartValue::class.createType()
    val KSmartValuedType = SmartValued::class.createType()
    val KEnumLikeType = EnumLike::class.createType()
    val KContentType = Content::class.createType()
    val KDecStringType = EncString::class.createType()
    val KDecIntType = EncInt::class.createType()
    val KDecLongType = EncLong::class.createType()
    val KDecDoubleType = EncDouble::class.createType()
    val KAnyType = Any::class.createType()

    fun getClassFromType(tpe: KType): KClass<*> {
        return when (tpe) {
        // Basic types
            KStringType -> KStringClass
            KBoolType -> KBoolClass
            KShortType -> KShortClass
            KIntType -> KIntClass
            KLongType -> KLongClass
            KFloatType -> KFloatClass
            KDoubleType -> KDoubleClass
            KDateTimeType -> KDateTimeClass
            KLocalDateType -> KLocalDateClass
            KLocalTimeType -> KLocalTimeClass
            KLocalDateTimeType -> KLocalDateTimeClass
            KZonedDateTimeType -> KZonedDateTimeClass
            KInstantType -> KInstantClass
            KUUIDType -> KUUIDClass
            KUPIDType -> KUPIDClass
            KDocType -> KDocClass
            KVarsType -> KVarsClass
            KSmartValueType -> KSmartValueClass
            KContentType -> KContentClass
            KDecStringType -> KDecStringClass
            KDecIntType -> KDecIntClass
            KDecLongType -> KDecLongClass
            KDecDoubleType -> KDecDoubleClass
            else -> tpe.classifier as KClass<*>
        }
    }

    fun getTypeExample(name: String, tpe: KType, textSample: String = "'abc'"): String {
        return when (tpe) {
        // Basic types
            KStringType -> textSample
            KBoolType -> "true"
            KShortType -> "0"
            KIntType -> "10"
            KLongType -> "100"
            KFloatType -> "10.0"
            KDoubleType -> "10.00"
            KDateTimeType -> DateTime.now().toStringNumeric("")
            KLocalDateType -> DateTime.now().toStringYYYYMMDD("")
            KLocalTimeType -> DateTime.now().toStringTime("")
            KLocalDateTimeType -> DateTime.now().toStringNumeric()
            KZonedDateTimeType -> DateTime.now().toStringNumeric()
            KInstantType -> DateTime.now().toStringNumeric()
            KUUIDType -> "782d1a4a-9223-4c49-96ee-cecb4c368a61"
            KUPIDType -> "prefix:782d1a4a-9223-4c49-96ee-cecb4c368a61"
            KDocType -> "user://myapp/conf/abc.conf"
            KVarsType -> "a=1,b=2,c=3"
            KSmartValueType -> "123-456-7890"
            KContentType -> "john@abc.com"
            KDecStringType -> "ALK342481SFA"
            KDecIntType -> "ALK342481SFA"
            KDecLongType -> "ALK342481SFA"
            KDecDoubleType -> "ALK342481SFA"
            else -> name
        }
    }

    fun isBasicType(tpe: KType): Boolean {
        return when (tpe) {
        // Basic types
            KStringType -> true
            KBoolType -> true
            KShortType -> true
            KIntType -> true
            KLongType -> true
            KFloatType -> true
            KDoubleType -> true
            KDateTimeType -> true
            KLocalDateType -> true
            KLocalTimeType -> true
            KLocalDateTimeType -> true
            KZonedDateTimeType -> true
            KInstantType -> true
            KUUIDType -> true
            KUPIDType -> true
            KSmartValueType -> true
            KDecStringType -> true
            KDecIntType -> true
            KDecLongType -> true
            KDecDoubleType -> true
            else -> false
        }
    }

    fun isBasicType(cls: KClass<*>): Boolean {
        return when (cls) {
        // Basic types
            KStringClass -> true
            KBoolClass -> true
            KShortClass -> true
            KIntClass -> true
            KLongClass -> true
            KFloatClass -> true
            KDoubleClass -> true
            KDateTimeClass -> true
            KLocalDateClass -> true
            KLocalTimeClass -> true
            KLocalDateTimeClass -> true
            KZonedDateTimeClass -> true
            KInstantClass -> true
            KUUIDClass -> true
            KUPIDClass -> true
            KSmartValueClass -> true
            KDecStringClass -> true
            KDecIntClass -> true
            KDecLongClass -> true
            KDecDoubleClass -> true
            else -> false
        }
    }
}
