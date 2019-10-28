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
package slatekit.notifications.push

import slatekit.common.EnumLike
import slatekit.common.EnumSupport

sealed class Platform(
    override val name: String,
    override val value: Int
) : EnumLike {

    object IOS     : Platform("ios", 0)
    object And     : Platform("android", 1)
    object Web     : Platform("web", 2)
    object None    : Platform("none", 3)
    data class Unknown(val text:String) : Platform("unknown", 5)


    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(IOS, And, Web, None)
        }

        override fun isUnknownSupported(): Boolean {
            return true
        }

        override fun unknown(name: String): EnumLike {
            return Unknown(name)
        }

        override fun unknown(value: Int): EnumLike {
            return Unknown("unknown")
        }
    }
}