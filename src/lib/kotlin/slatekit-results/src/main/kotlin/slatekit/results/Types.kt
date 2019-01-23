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
package slatekit.results


/**
 * Default implementation of Code
 */
data class Status(override val code: Int, override val msg: String) : Code


data class Invalid(override val code: Int, override val msg: String, override val ex:Exception? = null) : Err


data class Unknown(override val code: Int, override val msg: String, override val ex: Exception? = null) : Err

