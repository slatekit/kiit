package slatekit.apis

import slatekit.apis.setup.Parentable

/* ktlint-disable */
object AccessLevel {
     /**
      * Reference to a parent value
      * e.g. If set on Action, this refers to its parent API
     */
    const val PARENT = ApiConstants.parent


    /**
     * For external clients
     * NOTES:
     * 1. Client SDK enabled
     * 2. API Discovery enabled
     */
    const val PUBLIC = "public"


    /**
     * For internal company / server to server use.
     * NOTES:
     * 1. Client SDK disabled
     * 2. API Discovery enabled
     */
    const val INTERNAL = "internal"


    /**
     * PRIVATE : No docs will be generated, discovery is prevented
     * NOTES:
     * 1. Client SDK disabled
     * 2. API Discovery disabled
     * 3. Additional API key required
     */
    const val PRIVATE = "private"
}



sealed class Access(val value:Int, override val name:String)  : Parentable<Access> {
    object Private  : Access(0, AccessLevel.PRIVATE)
    object Internal : Access(1, AccessLevel.INTERNAL)
    object Public   : Access(2, AccessLevel.PUBLIC)
    object Parent   : Access(3, AccessLevel.PARENT)

    fun min(other:Access?):Access {
        return if(other != null && other.value < this.value) other else this
    }

    companion object  {

        fun parse(name:String): Access {
            return when(name) {
                AccessLevel.INTERNAL -> Access.Internal
                AccessLevel.PARENT   -> Access.Parent
                AccessLevel.PRIVATE  -> Access.Private
                AccessLevel.PUBLIC   -> Access.Public
                else                 -> Access.Private
            }
        }
    }
}
/* ktlint-enable */
