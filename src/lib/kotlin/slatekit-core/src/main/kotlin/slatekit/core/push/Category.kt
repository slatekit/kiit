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

interface Category {
    val name:String
}

object Alert   : Category { override val name = "alert"   }
object Share   : Category { override val name = "share"   }
object Reg     : Category { override val name = "reg"     }

data class OtherCategory(override val name:String): Category

