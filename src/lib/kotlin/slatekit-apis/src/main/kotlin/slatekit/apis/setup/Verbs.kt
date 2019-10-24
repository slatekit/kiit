package slatekit.apis.setup

object Verbs {
    const val get = "get"
    const val post = "post"
    const val put = "put"
    const val patch = "patch"
    const val delete = "delete"
    const val create = "create"
    const val update = "update"

    /**
     * This allows for automatically determining the verb based on the method name
     * e.g.
     * 1. getXX    = "get"
     * 2. createXX = "post"
     * 3. updateXX = "put"
     * 4. deleteXX = "delete"
     * 5. patchXX  = "patch"
     */
    const val auto = "auto"
}



sealed class Verb(val name:String) {
    object Auto   : Verb(Verbs.auto)
    object Read   : Verb(Verbs.get)
    object Create : Verb(Verbs.create)
    object Update : Verb(Verbs.update)
    object Delete : Verb(Verbs.delete)
}