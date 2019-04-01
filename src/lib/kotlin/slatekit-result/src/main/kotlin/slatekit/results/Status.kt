/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
philosophy: Simplicity above all else
</slate_header>
 */

package slatekit.results


/**
 * Interface to represent a Status Code with both an integer and descriptive message
 * Default implementations are available in [StatusGroup]
 * @sample :
 * { code: 8000, msg: "Invalid request" }
 * { code: 8001, msg: "Unauthorized"    }
 *
 * NOTE: A good example would be Http Status Codes
 */
interface Status {
    val code: Int
    val msg: String
}



/**
 * Provides an interface to mark a class / interface as convertible to an Http Status code
 */
interface HttpCode {
    fun toHttpCode():Int
}