package slatekit.data.features

import slatekit.common.data.Op

/**
 * Support patching of records by conditions
 */
interface Patchable<TId, T> : Inspectable<TId, T> where TId : Comparable<TId>, T:Any {

    /**
     * patches the items with the field and value supplied
     */
    fun patchByField(field: String, value: Any?): Int = patchByFields(listOf(Pair(field, value)), listOf())


    /**
     * Patch 1 item by its id using the updates provided
     */
    fun patchById(id: TId, updates: List<Pair<String, Any?>>): Int = patchByFields(updates, listOf(Triple(meta.pkey.name, Op.Eq, id)))


    /**
     * Patch all items with the matching conditions
     */
    fun patchByFields(fields:List<Pair<String, Any?>>, conditions: List<Triple<String, Op, Any?>>): Int
}
