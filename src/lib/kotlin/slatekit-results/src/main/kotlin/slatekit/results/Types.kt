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
 * Represents any type of successful status code
 */
data class Successful(override val code: Int, override val msg: String) : Code


/**
 * Represents a filter out error code
 * NOTE: This is a unique case ( depending on context ) is somewhat in-between a success / error.
 * For example:
 */
data class Filtered(override val code: Int, override val msg: String, val ex: Exception? = null) : Err


/**
 * Represents an invalid/bad request error code
 * NOTE: This can be useful for :
 * 1. request validation
 * 2. parameter validation
 */
data class Invalid(override val code: Int, override val msg: String, val ex: Exception? = null) : Err


data class Unexpected(override val code: Int, override val msg: String, val ex: Exception? = null) : Err


class Unhandled(override val msg: String, override val code: Int = 500) : Exception(msg), Err

