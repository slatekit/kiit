/**
 <kiit_header>
url: www.kiit.dev
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package kiit.common.auth

object Roles {
    const val ALL = "*"
    const val GUEST = "?"
    const val PARENT = "@parent"
    const val NONE = "@none"
}
