package slatekit.apis.setup


object AccessLevel {
    /**
     * For external user
     */
    const val Public = "public"


    /**
     * For internal company / server to server use
     */
    const val Internal = "internal"


    /**
     * Private : No docs will be generated, discovery is prevented
     */
    const val Private = "private"
}



sealed class Access(val name:String) {
    object Public   : Access(AccessLevel.Private)
    object Internal : Access(AccessLevel.Internal)
    object Private  : Access(AccessLevel.Private)
}