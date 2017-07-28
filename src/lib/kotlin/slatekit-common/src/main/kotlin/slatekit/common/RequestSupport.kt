package slatekit.common

/**
 * Support class for the abstract request
 */
interface RequestSupport {
    fun raw():Any?
    fun getDoc(name:String): Doc
}