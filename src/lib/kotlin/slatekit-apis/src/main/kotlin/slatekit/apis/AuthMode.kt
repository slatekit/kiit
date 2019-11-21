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
package slatekit.apis

import slatekit.apis.setup.Parentable

/* ktlint-disable */
object AuthModes {
    /**
     * Reference to a parent value
     * e.g. If set on Action, this refers to its parent API
     */
    const val PARENT = ApiConstants.parent


    /**
     * To represent a token based authentication where there is a unique token per user
     */
    const val TOKEN = "token"



    /**
     * To represent an api-key based authentication where many clients/users can share an api key
     */
    const val KEYED = "keyed"


    /**
     * For custom setup / e.g. basic auth, what ever
     */
    const val CUSTOM = "custom"


    /**
     * For no authentication, e.g. public access ( like health check/version )
     */
    const val NONE = "none"
}



sealed class AuthMode(override val name:String)  : Parentable<AuthMode> {
    object None   : AuthMode(AuthModes.NONE)
    object Parent : AuthMode(AuthModes.PARENT)
    object Token  : AuthMode(AuthModes.TOKEN)
    object Keyed  : AuthMode(AuthModes.KEYED)
    object Custom : AuthMode(AuthModes.CUSTOM)



    companion object  {

        fun parse(name:String): AuthMode {
            return when(name) {
                AuthModes.NONE   -> AuthMode.None
                AuthModes.PARENT -> AuthMode.Parent
                AuthModes.TOKEN  -> AuthMode.Token
                AuthModes.KEYED  -> AuthMode.Keyed
                else             -> AuthMode.Custom
            }
        }
    }
}
/* ktlint-enable */
