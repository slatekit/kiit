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

/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */


/**
 * Experimental - Do Not Use
 */
@Suppress("UNCHECKED_CAST")
@Deprecated(message = "Experimental", level = DeprecationLevel.WARNING)
sealed class ResultE<out L, out R> {

    abstract val success: Boolean
    abstract val code: Int
    abstract val msg: String?
    abstract val err: Exception?
}

data class Right<out R>(val value: R,
                       override val code: Int,
                       override val msg: String?) : ResultE<Nothing, R>() {

    override val success: Boolean get() = true
    override val err: Exception? get() = null
}


data class Left<out L>(val value: L,
                        override val code: Int,
                        override val err: Exception?,
                        override val msg: String?) : ResultE<L, Nothing>() {

    override val success: Boolean get() = true
}
