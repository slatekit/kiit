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
package slatekit.common.results

interface Err {
    val code: Int
    val msg: String
    val err: Exception?
}

/**
 * Default implementation of Err
 *
 * For Anonymous creation:
 * object : Err { override val code = 400; override val msg = "Invalid error"; override val err:Exception? = null }
 */
data class ErrorInfo(override val code: Int,
                override val msg: String,
                override val err: Exception? = null) : Err


