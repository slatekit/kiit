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

object Types {

    val JStringClass         = ("").javaClass
    val JBoolClass           = (true).javaClass
    val JShortClass          = (0.toShort()).javaClass
    val JIntClass            = (0).javaClass
    val JLongClass           = (0L).javaClass
    val JFloatClass          = (0.toFloat()).javaClass
    val JDoubleClass         = (0.0).javaClass
    val JDateTimeClass       = DateTime.MIN.javaClass
    val JLocalDateClass      = LocalDate.MIN.javaClass
    val JLocalTimeClass      = LocalTime.MIN.javaClass
    val JLocalDateTimeClass  = LocalDateTime.MIN.javaClass
    val JZonedDateTimeClass  = ZonedDateTime.now().javaClass
    val JInstantClass        = Instant.MIN.javaClass
    val JDocClass            = Doc.javaClass
    val JVarsClass           = Vars.javaClass
    val JSmartStringClass    = SmartString.javaClass
    val JDecIntClass         = DecInt.javaClass
    val JDecLongClass        = DecLong.javaClass
    val JDecDoubleClass      = DecDouble.javaClass
    val JDecStringClass      = DecString.javaClass
}