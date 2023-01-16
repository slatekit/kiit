/**
 <kiit_header>
url: www.slatekit.com
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package kiit.comms.push

import kiit.common.EnumLike
import kiit.common.EnumSupport

sealed class Platform(
    override val name: String,
    override val value: Int
) : EnumLike {

    object IOS : Platform("ios", 0)
    object And : Platform("android", 1)
    object Web : Platform("web", 2)
    object None : Platform("none", 3)
    data class Unknown(val text: String) : Platform("unknown", 5)

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
