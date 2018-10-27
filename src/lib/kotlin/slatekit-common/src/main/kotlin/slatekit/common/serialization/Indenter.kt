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

package slatekit.common.serialization

import java.util.concurrent.atomic.AtomicInteger

class Indenter {
    val count = AtomicInteger()

    fun value(): String = "\t".repeat(count.get())

    fun inc(): Int = count.incrementAndGet()

    fun dec(): Int = count.decrementAndGet()
}
