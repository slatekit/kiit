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
package slatekit.apis.setup



object AuthModes {

    /**
     * To represent a token based authentication where there is a unique token per user
     */
    const val token = "token"


    /**
     * To represent an api-key based authentication where many clients/users can share an api key
     */
    const val keyed = "keyed"


    /**
     * For custom setup / e.g. basic auth, what ever
     */
    const val custom = "custom"
}



sealed class AuthMode(val name:String) {
    object Token  : AuthMode(AuthModes.token)
    object Keyed  : AuthMode(AuthModes.keyed)
    object Custom : AuthMode(AuthModes.custom)
}