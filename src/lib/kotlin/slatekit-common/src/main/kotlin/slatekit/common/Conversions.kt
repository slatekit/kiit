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

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KType


/**
 * Conversions from text to types
 * NOTE: While the standard library has extension methods
 * to convert string to a certain type, these are here to
 * support lamdas using in other places
 */
object Conversions {

    fun toString(text: String): String = text

    fun toBool(text: String): Boolean = text.toBoolean()

    fun toShort(text: String): Short = text.toShort()

    fun toInt(text: String): Int = text.toInt()

    fun toLong(text: String): Long = text.toLong()

    fun toFloat(text: String): Float = text.toFloat()

    fun toDouble(text: String): Double = text.toDouble()

    fun toLocalDate(text: String): LocalDate = LocalDate.parse(text)

    fun toLocalTime(text: String): LocalTime = LocalTime.parse(text)

    fun toLocalDateTime(text: String): LocalDateTime = LocalDateTime.parse(text)

    fun toZonedDateTime(text: String): ZonedDateTime = ZonedDateTime.parse(text)

    fun toDateTime(text: String): DateTime = InputFuncs.convertDate(text)


    fun converterFor(tpe: KClass<*>): (String) -> Any {
        val converter = when (tpe) {
        // Basic types
            Types.BoolClass          -> this::toBool
            Types.ShortClass         -> this::toShort
            Types.IntClass           -> this::toInt
            Types.LongClass          -> this::toLong
            Types.FloatClass         -> this::toFloat
            Types.DoubleClass        -> this::toDouble
            Types.LocalDateClass     -> this::toLocalDate
            Types.LocalTimeClass     -> this::toLocalTime
            Types.LocalDateTimeClass -> this::toLocalDateTime
            Types.ZonedDateTimeClass -> this::toZonedDateTime
            Types.DateTimeClass      -> this::toDateTime
            Types.StringClass        -> this::toString
            else                     -> this::toString
        }
        return converter
    }


    fun converterFor(tpe: KType): (String) -> Any {
        val converter = when (tpe) {
        // Basic types
            Types.StringType        -> this::toString
            Types.BoolType          -> this::toBool
            Types.ShortType         -> this::toShort
            Types.IntType           -> this::toInt
            Types.LongType          -> this::toLong
            Types.FloatType         -> this::toFloat
            Types.DoubleType        -> this::toDouble
            Types.LocalDateType     -> this::toLocalDate
            Types.LocalTimeType     -> this::toLocalTime
            Types.LocalDateTimeType -> this::toLocalDateTime
            Types.ZonedDateTimeClass -> this::toZonedDateTime
            Types.DateTimeClass     -> this::toDateTime
            else                    -> this::toString
        }
        return converter
    }
}