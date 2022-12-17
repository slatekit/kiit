package kiit.apis

import kiit.apis.setup.Parentable

/* ktlint-disable */
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
    const val AUTO = "auto"


    /**
     * Core operations supported
     */
    const val GET = "get"
    const val CREATE = "create"
    const val UPDATE = "update"
    const val DELETE = "delete"


    /**
     * Here for compatibility with HTTP/REST
     */
    const val POST  = "post"
    const val PUT   = "put"
    const val PATCH = "patch"
    const val PARENT = ApiConstants.parent
}


sealed class Verb(override val name: String)  : Parentable<Verb> {
    object Auto   : Verb(Verbs.AUTO)
    object Get    : Verb(Verbs.GET)
    object Create : Verb(Verbs.CREATE)
    object Update : Verb(Verbs.UPDATE)
    object Put    : Verb(Verbs.PUT)
    object Post   : Verb(Verbs.POST)
    object Patch  : Verb(Verbs.PATCH)
    object Delete : Verb(Verbs.DELETE)
    object Parent : Verb(Verbs.PARENT)


    fun isMatch(other:String):Boolean = this.name == other

    companion object {

        fun parse(name:String): Verb {
            return when(name) {
                Verbs.AUTO   -> Verb.Auto
                Verbs.GET    -> Verb.Get
                Verbs.CREATE -> Verb.Create
                Verbs.UPDATE -> Verb.Update
                Verbs.PUT    -> Verb.Put
                Verbs.POST   -> Verb.Post
                Verbs.PATCH  -> Verb.Patch
                Verbs.DELETE -> Verb.Delete
                Verbs.PARENT -> Verb.Parent
                else         -> Verb.Auto
            }
        }
    }
}
/* ktlint-enable */
