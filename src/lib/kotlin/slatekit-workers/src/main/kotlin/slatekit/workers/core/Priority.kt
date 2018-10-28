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
package slatekit.workers.core

import slatekit.common.EnumLike
import slatekit.common.EnumSupport

/**
 * Allows for setting a priority on the queues
 */
enum class Priority(override val value: Int) : EnumLike {
    Low(1),
    Medium(2),
    High(3);

    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(Low, Medium, High)
        }
    }
}
