package slatekit.data.features

import slatekit.data.core.Table
import slatekit.query.Op

/**
 * Support patching of records by conditions
 */
interface Patchable<TId, T> : Table<TId> where TId : Comparable<TId> {

    /**
     * Patch 1 item by its id using the updates provided
     */
    fun patchById(id: TId, updates: List<Pair<String, Any?>>): Int = patchByFields(updates, listOf(Triple(id(), Op.Eq, id)))


    /**
     * Patch all items with the matching conditions
     */
    fun patchByFields(fields:List<Pair<String, Any?>>, conditions: List<Triple<String, Op, Any?>>): Int
}
