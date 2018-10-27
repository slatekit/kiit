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
package slatekit.core.push

import slatekit.common.EnumLike
import slatekit.common.EnumSupport

data class Platform(
    override val name: String,
    override val value: Int
) : EnumLike {

    companion object : EnumSupport() {

        val PlatformIOS = Platform("ios", 0)
        val PlatformAnd = Platform("android", 1)
        val PlatformWeb = Platform("web", 2)
        val PlatformSrv = Platform("server", 3)
        val PlatformNone = Platform("none", 4)

        override fun all(): Array<EnumLike> {
            return arrayOf(PlatformIOS, PlatformAnd, PlatformWeb, PlatformWeb, PlatformSrv, PlatformNone)
        }

        override fun isUnknownSupported(): Boolean {
            return true
        }

        override fun unknown(name: String): EnumLike {
            return Platform(name, 6)
        }

        override fun unknown(value: Int): EnumLike {
            return Platform("unknown", 6)
        }
    }
}