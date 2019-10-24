package slatekit.apis.setup

object Verbs {


    /**
     * This allows for automatically determining the verb based on the method name
     * e.g.
     * 1. getXX    = "get"
     * 2. createXX = "post"
     * 3. updateXX = "put"
     * 4. deleteXX = "delete"
     * 5. patchXX  = "patch"
     */
    const val Auto = "auto"


    /**
     * Core operations supported
     */
    const val Read = "read"
    const val Create = "create"
    const val Update = "update"
    const val Delete = "delete"


    /**
     * Here for compatibility with HTTP/REST
     */
    const val Post  = "post"
    const val Put   = "put"
    const val Patch = "patch"
}



sealed class Verb(val name:String) {
    object Auto   : Verb(Verbs.Auto)
    object Read   : Verb(Verbs.Read)
    object Create : Verb(Verbs.Create)
    object Update : Verb(Verbs.Update)
    object Put    : Verb(Verbs.Put)
    object Post   : Verb(Verbs.Post)
    object Patch  : Verb(Verbs.Patch)
    object Delete : Verb(Verbs.Delete)
}