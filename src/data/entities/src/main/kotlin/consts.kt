/**
 <kiit_header>
url: www.kiit.dev
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
/**
 * Created by kishorereddy on 5/19/17.
 */

package kiit.entities

// import java.time.format.DateTimeFormatter
import org.threeten.bp.format.*

object SchemaConstants {
    const val UNLIMITED: Int = -1
}

object Consts {

    val version = "0.9.0"
    val NULL = "NULL"
    val idCol = "id"
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
}
