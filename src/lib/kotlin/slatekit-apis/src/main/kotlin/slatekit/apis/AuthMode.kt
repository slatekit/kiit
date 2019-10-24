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


object AuthModes {
    /**
     * Reference to a parent value
     * e.g. If set on Action, this refers to its parent API
     */
    const val Parent = ApiConstants.parent


    /**
     * To represent a token based authentication where there is a unique token per user
     */
    const val Token = "token"



    /**
     * To represent an api-key based authentication where many clients/users can share an api key
     */
    const val Keyed = "keyed"


    /**
     * For custom setup / e.g. basic auth, what ever
     */
    const val Custom = "custom"


    /**
     * For no authentication, e.g. public access ( like health check/version )
     */
    const val None = "none"
}



sealed class AuthMode(override val name:String)  : Parentable<AuthMode> {
    object None   : AuthMode(AuthModes.None)
    object Parent : AuthMode(AuthModes.Parent)
    object Token  : AuthMode(AuthModes.Token)
    object Keyed  : AuthMode(AuthModes.Keyed)
    object Custom : AuthMode(AuthModes.Custom)



    companion object  {

        fun parse(name:String): AuthMode {
            return when(name) {
                AuthModes.None   -> AuthMode.None
                AuthModes.Parent -> AuthMode.Parent
                AuthModes.Token  -> AuthMode.Token
                AuthModes.Keyed  -> AuthMode.Keyed
                else             -> AuthMode.Custom
            }
        }
    }
}