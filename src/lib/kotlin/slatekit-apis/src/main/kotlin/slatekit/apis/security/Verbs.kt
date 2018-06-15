package slatekit.apis.security

object Verbs {
    const val all = "*"
    const val get = "get"
    const val post = "post"
    const val put = "put"
    const val patch = "patch"
    const val delete = "delete"

    /**
     * This allows for automatically determining the verb based on the method name
     * e.g.
     * 1. getXX    = "get"
     * 2. createXX = "post"
     * 3. updateXX = "post"
     * 4. deleteXX = "post"
     * 5. patchXX  = "post"
     */
    const val auto = "auto"


    /**
     * This allows for automatically determining the verb based on the method name
     * e.g.
     * 1. getXX    = "get"
     * 2. createXX = "post"
     * 3. updateXX = "put"
     * 4. deleteXX = "delete"
     * 5. patchXX  = "patch"
     */
    const val rest = "rest"
}