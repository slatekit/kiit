package kiit.data.features

import kiit.common.data.Value

/**
 * Provides support for calling stored procs
 */
interface Scriptable<TId, T> where TId : Comparable<TId> {

    /**
     * create item using the proc and args
     */
    fun createByProc(name: String, args: List<Value>? = null): TId


    /**
     * updates items using the proc and args
     */
    fun updateByProc(name: String, args: List<Value>? = null): Long


    /**
     * updates items using the proc and args
     */
    fun deleteByProc(name: String, args: List<Value>? = null): Long


    /**
     * updates items using the proc and args
     */
    fun findByProc(name: String, args: List<Value>? = null): List<T>?
}
