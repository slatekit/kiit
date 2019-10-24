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
     * Reference to a parent value
     * e.g. If set on Action, this refers to its parent API
     */
    const val Parent = "@parent"


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



sealed class AuthMode(val name:String) {
    object None   : AuthMode(AuthModes.None)
    object Parent : AuthMode(AuthModes.Parent)
    object Token  : AuthMode(AuthModes.Token)
    object Keyed  : AuthMode(AuthModes.Keyed)
    object Custom : AuthMode(AuthModes.Custom)
}