package slatekit.apis.setup


object AccessLevel {
     /**
      * Reference to a parent value
      * e.g. If set on Action, this refers to its parent API
     */
    const val Parent = "@parent"


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



sealed class Access(val name:String) {
    object Parent   : Access(AccessLevel.Parent)
    object Public   : Access(AccessLevel.Private)
    object Internal : Access(AccessLevel.Internal)
    object Private  : Access(AccessLevel.Private)
}