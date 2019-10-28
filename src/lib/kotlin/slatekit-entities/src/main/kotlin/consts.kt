/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
/**
 * Created by kishorereddy on 5/19/17.
 */

package slatekit.entities

// import java.time.format.DateTimeFormatter
import org.threeten.bp.format.*

object Consts {

    val version = "0.9.0"
    val NULL = "NULL"
    val idCol = "id"
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
}
