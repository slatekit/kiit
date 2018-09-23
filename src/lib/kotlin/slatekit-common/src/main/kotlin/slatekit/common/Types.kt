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

import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.encrypt.EncString
import java.time.*

object Types {

    val JStringClass         = "".javaClass
    val JBoolClass           = true.javaClass
    val JShortClass          = 0.toShort().javaClass
    val JIntClass            = 0.javaClass
    val JLongClass           = 0L.javaClass
    val JFloatClass          = 0.toFloat().javaClass
    val JDoubleClass         = 0.0.javaClass
    val JDecimalClass        = 0.0.toBigDecimal().javaClass
    val JDateTimeClass       = DateTime.MIN.javaClass
    val JLocalDateClass      = LocalDate.MIN.javaClass
    val JLocalTimeClass      = LocalTime.MIN.javaClass
    val JLocalDateTimeClass  = LocalDateTime.MIN.javaClass
    val JZonedDateTimeClass  = ZonedDateTime.now().javaClass
    val JInstantClass        = Instant.MIN.javaClass
    val JDocClass            = Doc.javaClass
    val JVarsClass           = Vars.javaClass
    val JSmartStringClass    = SmartString.javaClass
    val JDecIntClass         = EncInt.javaClass
    val JDecLongClass        = EncLong.javaClass
    val JDecDoubleClass      = EncDouble.javaClass
    val JDecStringClass      = EncString.javaClass


    val JStringAnyClass         = ("" as Any).javaClass
    val JBoolAnyClass           = (true as Any).javaClass
    val JShortAnyClass          = (0.toShort() as Any).javaClass
    val JIntAnyClass            = (0 as Any).javaClass
    val JLongAnyClass           = (0L as Any).javaClass
    val JFloatAnyClass          = (0.toFloat() as Any).javaClass
    val JDoubleAnyClass         = (0.0 as Any).javaClass
    val JDecimalAnyClass        = (0.0.toBigDecimal() as Any).javaClass
    val JDateTimeAnyClass       = (DateTime.MIN as Any).javaClass
    val JLocalDateAnyClass      = (LocalDate.MIN as Any).javaClass
    val JLocalTimeAnyClass      = (LocalTime.MIN as Any).javaClass
    val JLocalDateTimeAnyClass  = (LocalDateTime.MIN as Any).javaClass
    val JZonedDateTimeAnyClass  = (ZonedDateTime.now() as Any).javaClass
    val JInstantAnyClass        = (Instant.MIN as Any).javaClass
}