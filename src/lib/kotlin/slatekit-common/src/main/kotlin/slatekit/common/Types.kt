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

import slatekit.common.types.Doc
import slatekit.common.crypto.EncDouble
import slatekit.common.crypto.EncInt
import slatekit.common.crypto.EncLong
import slatekit.common.crypto.EncString
import org.threeten.bp.*
import slatekit.common.ids.ULIDs
import slatekit.common.ids.UPIDs
import slatekit.common.values.Vars
import java.util.*

object Types {

    val JCharClass = Char.javaClass
    val JStringClass = String::class.java
    val JBoolClass = Boolean::class.java
    val JShortClass = Short::class.java
    val JIntClass = Int::class.java
    val JLongClass = Long::class.java
    val JFloatClass = Float::class.java
    val JDoubleClass = Double::class.java
    val JDateTimeClass = DateTimes.MIN.javaClass
    val JLocalDateClass = LocalDate.MIN.javaClass
    val JLocalTimeClass = LocalTime.MIN.javaClass
    val JLocalDateTimeClass = LocalDateTime.MIN.javaClass
    val JZonedDateTimeClass = ZonedDateTime.now().javaClass
    val JInstantClass = Instant.MIN.javaClass
    val JUUIDClass = UUID.fromString("1bd16d06-a45a-45d8-b4c9-7e864251275f").javaClass
    val JULIDClass = ULIDs.create().javaClass
    val JUPIDClass = UPIDs.create().javaClass
    val JDocClass = Doc.javaClass
    val JVarsClass = Vars.javaClass
    val JDecIntClass = EncInt.javaClass
    val JDecLongClass = EncLong.javaClass
    val JDecDoubleClass = EncDouble.javaClass
    val JDecStringClass = EncString.javaClass


    val JIntAnyClass = (0 as Any).javaClass
}
