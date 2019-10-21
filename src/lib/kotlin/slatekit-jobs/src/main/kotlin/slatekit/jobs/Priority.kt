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
package slatekit.jobs

import slatekit.common.EnumLike
import slatekit.common.EnumSupport

/**
 * General purpose priority Enum to be used for Queue to indicate
 * that 1 queue has a higher/lower priority that another queue and
 * therefore will be processed more often.
 */
enum class Priority(override val value: Int) : EnumLike {
    Low(1),
    Mid(2),
    High(3);

    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(Low, Mid, High)
        }
    }
}
