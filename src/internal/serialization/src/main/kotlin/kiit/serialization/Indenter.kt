/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.serialization

import java.util.concurrent.atomic.AtomicInteger

class Indenter {
    val count = AtomicInteger()

    fun value(): String = "\t".repeat(count.get())

    fun inc(): Int = count.incrementAndGet()

    fun dec(): Int = count.decrementAndGet()
}
