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


    /**
     * Builds a Vars object which is essentially a lookup of items by both index and key
     * @param args
     * @param paramName
     * @return
     */
    fun toVars(data:Any?): Vars {
        return when(data) {
            null      -> Vars.apply("")
            "null"    -> Vars.apply("")
            is String -> if(data.isNullOrEmpty()) Vars.apply("") else Vars.apply(data)
            else      -> Vars.apply("")
        }
    }


    /**
     * Builds a Doc object by reading the file content from the referenced uri
     * e.g.
     * 1. "user://slatekit/temp/file1.txt"    reference user directory
     * 2. "file://c:/slatekit/temp/file.txt"  reference file explicitly
     * @param args
     * @param paramName
     * @return
     */
    fun toDoc(uri:String): Doc {
        val doc = Uris.readDoc(uri)
        return doc ?: Doc.text("", "")
    }


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
            Types.DocClass           -> this::toDoc
            Types.VarsClass          -> this::toVars
            else                     -> this::toString
        }
        return converter
    }


    fun converterFor(tpe: KType): (String) -> Any {
        val converter = when (tpe) {
        // Basic types
            Types.StringType         -> this::toString
            Types.BoolType           -> this::toBool
            Types.ShortType          -> this::toShort
            Types.IntType            -> this::toInt
            Types.LongType           -> this::toLong
            Types.FloatType          -> this::toFloat
            Types.DoubleType         -> this::toDouble
            Types.LocalDateType      -> this::toLocalDate
            Types.LocalTimeType      -> this::toLocalTime
            Types.LocalDateTimeType  -> this::toLocalDateTime
            Types.ZonedDateTimeClass -> this::toZonedDateTime
            Types.DateTimeClass      -> this::toDateTime
            Types.DocType            -> this::toDoc
            Types.VarsType           -> this::toVars
            else                     -> this::toString
        }
        return converter
    }


    fun convert(key:String, paramType: KType, rawVal:Any?, decryptor:((String) -> String)? = null): Any {

        val result = when (paramType) {
            // Basic types
            Types.StringType        -> handleString(rawVal)
            Types.BoolType          -> rawVal.toString().toBoolean()
            Types.ShortType         -> rawVal.toString().toShort()
            Types.IntType           -> rawVal.toString().toInt()
            Types.LongType          -> rawVal.toString().toLong()
            Types.FloatType         -> rawVal.toString().toFloat()
            Types.DoubleType        -> rawVal.toString().toDouble()
            Types.LocalDateType     -> Conversions.toLocalDate(rawVal as String)
            Types.LocalTimeType     -> Conversions.toLocalTime(rawVal as String)
            Types.LocalDateTimeType -> Conversions.toLocalDateTime(rawVal as String)
            Types.ZonedDateTimeType -> Conversions.toZonedDateTime(rawVal as String)
            Types.DateTimeType      -> Conversions.toDateTime(rawVal as String)
            Types.TypeDecInt        -> decryptor?.let { e -> DecInt(e(rawVal as String).toInt()) } ?: DecInt(0)
            Types.TypeDecLong       -> decryptor?.let { e -> DecLong(e(rawVal as String).toLong()) } ?: DecLong(0L)
            Types.TypeDecDouble     -> decryptor?.let { e -> DecDouble(e(rawVal as String).toDouble()) } ?: DecDouble(0.0)
            Types.TypeDecString     -> decryptor?.let { e -> DecString(e(rawVal as String)) } ?: DecString("")
            Types.DocType           -> Conversions.toDoc(rawVal.toString())
            Types.VarsType          -> Conversions.toVars(rawVal)

            // Complex types not supported: e.g. Lists/Maps/Nested objects
                else                    -> handleString(rawVal)
            }
        return result
    }


    /**
     * Builds a string parameter ensuring that nulls are avoided.
     * @param args
     * @param paramName
     * @return
     */
    fun handleString(data:Any?): String {
        // As a design choice, this marshaller will only pass empty string to
        // API methods instead of null
        return when(data) {
            null      -> ""
            "null"    -> ""
            is String -> if(data.isNullOrEmpty()) "" else data
            else      -> data.toString()
        }
    }
}