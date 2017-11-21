package slatekit.meta

import slatekit.common.*
import slatekit.common.encrypt.DecDouble
import slatekit.common.encrypt.DecInt
import slatekit.common.encrypt.DecLong
import slatekit.common.encrypt.DecString
import java.time.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object KTypes {

    val KStringClass         = String::class
    val KBoolClass           = Boolean::class
    val KShortClass          = Short::class
    val KIntClass            = Int::class
    val KLongClass           = Long::class
    val KFloatClass          = Float::class
    val KDoubleClass         = Double::class
    val KDateTimeClass       = DateTime::class
    val KLocalDateClass      = LocalDate::class
    val KLocalTimeClass      = LocalTime::class
    val KLocalDateTimeClass  = LocalDateTime::class
    val KZonedDateTimeClass  = ZonedDateTime::class
    val KInstantClass        = Instant::class
    val KDocClass            = Doc::class
    val KVarsClass           = Vars::class
    val KSmartStringClass    = SmartString::class
    val KContentClass        = Content::class
    val KDecStringClass      = DecString::class
    val KDecIntClass         = DecInt::class
    val KDecLongClass        = DecLong::class
    val KDecDoubleClass      = DecDouble::class
    val KAnyClass            = DecDouble::class


    val KStringType        = String::class.createType()
    val KBoolType          = Boolean::class.createType()
    val KShortType         = Short::class.createType()
    val KIntType           = Int::class.createType()
    val KLongType          = Long::class.createType()
    val KFloatType         = Float::class.createType()
    val KDoubleType        = Double::class.createType()
    val KDateTimeType      = DateTime::class.createType()
    val KLocalDateType     = LocalDate::class.createType()
    val KLocalTimeType     = LocalTime::class.createType()
    val KLocalDateTimeType = LocalDateTime::class.createType()
    val KZonedDateTimeType = ZonedDateTime::class.createType()
    val KInstantType       = Instant::class.createType()
    val KDocType           = Doc::class.createType()
    val KVarsType          = Vars::class.createType()
    val KSmartStringType   = SmartString::class.createType()
    val KContentType       = Content::class.createType()
    val KDecStringType     = DecString::class.createType()
    val KDecIntType        = DecInt::class.createType()
    val KDecLongType       = DecLong::class.createType()
    val KDecDoubleType     = DecDouble::class.createType()
    val KAnyType           = Any::class.createType()


    fun getClassFromType(tpe: KType): KClass<*> {
        return when (tpe) {
        // Basic types
            KStringType        -> KStringClass
            KBoolType          -> KBoolClass
            KShortType         -> KShortClass
            KIntType           -> KIntClass
            KLongType          -> KLongClass
            KFloatType         -> KFloatClass
            KDoubleType        -> KDoubleClass
            KDateTimeType      -> KDateTimeClass
            KLocalDateType     -> KLocalDateClass
            KLocalTimeType     -> KLocalTimeClass
            KLocalDateTimeType -> KLocalDateTimeClass
            KZonedDateTimeType -> KZonedDateTimeClass
            KInstantType       -> KInstantClass
            KDocType           -> KDocClass
            KVarsType          -> KVarsClass
            KSmartStringType   -> KSmartStringClass
            KContentType       -> KContentClass
            KDecStringType     -> KDecStringClass
            KDecIntType        -> KDecIntClass
            KDecLongType       -> KDecLongClass
            KDecDoubleType     -> KDecDoubleClass
            else              -> tpe.classifier as KClass<*>
        }
    }


    fun getTypeExample(name:String, tpe: KType, textSample:String = "'abc'"): String {
        return when (tpe) {
        // Basic types
            KStringType        -> textSample
            KBoolType          -> "true"
            KShortType         -> "0"
            KIntType           -> "10"
            KLongType          -> "100"
            KFloatType         -> "10.0"
            KDoubleType        -> "10.00"
            KDateTimeType      -> DateTime.now().toStringNumeric("")
            KLocalDateType     -> DateTime.now().toStringYYYYMMDD("")
            KLocalTimeType     -> DateTime.now().toStringTime("")
            KLocalDateTimeType -> DateTime.now().toStringNumeric()
            KZonedDateTimeType -> DateTime.now().toStringNumeric()
            KInstantType       -> DateTime.now().toStringNumeric()
            KDocType           -> "user://myapp/conf/abc.conf"
            KVarsType          -> "a=1,b=2,c=3"
            KSmartStringType   -> "123-456-7890"
            KContentType       -> "john@abc.com"
            KDecStringType     -> "ALK342481SFA"
            KDecIntType        -> "ALK342481SFA"
            KDecLongType       -> "ALK342481SFA"
            KDecDoubleType     -> "ALK342481SFA"
            else               -> name
        }
    }


    fun getTypeExampleValuePair(name:String, tpe: KType, textSample:String = "'abc'"): Pair<String, Any> {
        return when (tpe) {
        // Basic types
            KStringType        -> Pair(name, textSample)
            KBoolType          -> Pair(name, true)
            KShortType         -> Pair(name, 0.toShort())
            KIntType           -> Pair(name, 10)
            KLongType          -> Pair(name, 100L)
            KFloatType         -> Pair(name, 10.0.toFloat())
            KDoubleType        -> Pair(name, 10.00)
            KDateTimeType      -> Pair(name, DateTime.now())
            KLocalDateType     -> Pair(name, DateTime.now().local().toLocalDate())
            KLocalTimeType     -> Pair(name, DateTime.now().local().toLocalTime())
            KLocalDateTimeType -> Pair(name, DateTime.now().local())
            KZonedDateTimeType -> Pair(name, DateTime.now().raw)
            KInstantType       -> Pair(name, DateTime.now().raw.toInstant())
            KDocType           -> Pair(name, "user://myapp/conf/abc.conf")
            KVarsType          -> Pair(name, "a=1,b=2,c=3")
            KSmartStringType   -> Pair(name, "123-456-7890")
            KContentType       -> Pair(name, "john@abc.com")
            KDecStringType     -> Pair(name, "ALK342481SFA")
            KDecIntType        -> Pair(name, "ALK342481SFA")
            KDecLongType       -> Pair(name, "ALK342481SFA")
            KDecDoubleType     -> Pair(name, "ALK342481SFA")
            else               -> Pair(name, name)
        }
    }
}