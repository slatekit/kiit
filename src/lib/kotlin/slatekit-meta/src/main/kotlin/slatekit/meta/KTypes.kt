package slatekit.meta

import slatekit.common.DateTime
import slatekit.common.Doc
import slatekit.common.SmartString
import slatekit.common.Vars
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
    val KDecStringClass     = DecString::class
    val KDecIntClass        = DecInt::class
    val KDecLongClass       = DecLong::class
    val KDecDoubleClass     = DecDouble::class


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
    val KDecStringType     = DecString::class.createType()
    val KDecIntType        = DecInt::class.createType()
    val KDecLongType       = DecLong::class.createType()
    val KDecDoubleType     = DecDouble::class.createType()


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
            KDecStringType     -> KDecStringClass
            KDecIntType        -> KDecIntClass
            KDecLongType       -> KDecLongClass
            KDecDoubleType     -> KDecDoubleClass
            else              -> tpe.classifier as KClass<*>
        }
    }
}