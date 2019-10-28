package slatekit.apis

import slatekit.apis.setup.Parentable

/* ktlint-disable */
object AccessLevel {
     /**
      * Reference to a parent value
      * e.g. If set on Action, this refers to its parent API
     */
    const val Parent = ApiConstants.parent


    /**
     * For external clients
     * NOTES:
     * 1. Client SDK enabled
     * 2. API Discovery enabled
     */
    const val Public = "public"


    /**
     * For internal company / server to server use.
     * NOTES:
     * 1. Client SDK disabled
     * 2. API Discovery enabled
     */
    const val Internal = "internal"


    /**
     * Private : No docs will be generated, discovery is prevented
     * NOTES:
     * 1. Client SDK disabled
     * 2. API Discovery disabled
     * 3. Additional API key required
     */
    const val Private = "private"
}



sealed class Access(override val name:String)  : Parentable<Access> {
    object Parent   : Access(AccessLevel.Parent)
    object Public   : Access(AccessLevel.Private)
    object Internal : Access(AccessLevel.Internal)
    object Private  : Access(AccessLevel.Private)


    companion object  {

        fun parse(name:String): Access {
            return when(name) {
                AccessLevel.Internal -> Access.Internal
                AccessLevel.Parent   -> Access.Parent
                AccessLevel.Private  -> Access.Private
                AccessLevel.Public   -> Access.Public
                else                 -> Access.Private
            }
        }
    }
}
/* ktlint-enable */
