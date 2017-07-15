/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common

import slatekit.common.encrypt.DecDouble
import slatekit.common.encrypt.DecInt
import slatekit.common.encrypt.DecLong
import slatekit.common.encrypt.DecString
import java.time.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

object Types {

    val StringClass         = String::class
    val BoolClass           = Boolean::class
    val ShortClass          = Short::class
    val IntClass            = Int::class
    val LongClass           = Long::class
    val FloatClass          = Float::class
    val DoubleClass         = Double::class
    val DateTimeClass       = DateTime::class
    val LocalDateClass      = LocalDate::class
    val LocalTimeClass      = LocalTime::class
    val LocalDateTimeClass  = LocalDateTime::class
    val ZonedDateTimeClass  = ZonedDateTime::class
    val InstantClass        = Instant::class
    val DocClass            = Doc::class
    val VarsClass           = Vars::class


    val StringType        = String::class.createType()
    val BoolType          = Boolean::class.createType()
    val ShortType         = Short::class.createType()
    val IntType           = Int::class.createType()
    val LongType          = Long::class.createType()
    val FloatType         = Float::class.createType()
    val DoubleType        = Double::class.createType()
    val DateTimeType      = DateTime::class.createType()
    val LocalDateType     = LocalDate::class.createType()
    val LocalTimeType     = LocalTime::class.createType()
    val LocalDateTimeType = LocalDateTime::class.createType()
    val ZonedDateTimeType = ZonedDateTime::class.createType()
    val InstantType       = Instant::class.createType()
    val DocType           = Doc::class.createType()
    val VarsType          = Vars::class.createType()


    val TypeDecString = DecString::class.createType()
    val TypeDecInt = DecInt::class.createType()
    val TypeDecLong = DecLong::class.createType()
    val TypeDecDouble = DecDouble::class.createType()


    fun getClassFromType(tpe: KType): KClass<*> {
        return when (tpe) {
            // Basic types
            StringType        -> Types.StringClass
            BoolType          -> BoolClass
            ShortType         -> ShortClass
            IntType           -> IntClass
            LongType          -> LongClass
            FloatType         -> FloatClass
            DoubleType        -> DoubleClass
            DateTimeType      -> DateTimeClass
            LocalDateType     -> LocalDateClass
            LocalTimeType     -> LocalTimeClass
            LocalDateTimeType -> LocalDateTimeClass
            ZonedDateTimeType -> ZonedDateTimeClass
            InstantType       -> InstantClass
            DocType           -> DocClass
            VarsType          -> VarsClass
            else              -> tpe.classifier as KClass<*>
        }
    }
}