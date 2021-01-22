package slatekit.data.slatekit.data.features

/**
 * Provides support for calling stored procs
 */
interface Scriptable<TId, T> where TId : Comparable<TId> {

    /**
     * create item using the proc and args
     */
    fun createByProc(name: String, args: List<Any>? = null): TId


    /**
     * updates items using the proc and args
     */
    fun updateByProc(name: String, args: List<Any>? = null): Long


    /**
     * updates items using the proc and args
     */
    fun deleteByProc(name: String, args: List<Any>? = null): Long


    /**
     * updates items using the proc and args
     */
    fun findByProc(name: String, args: List<Any>? = null): List<T>?
}
