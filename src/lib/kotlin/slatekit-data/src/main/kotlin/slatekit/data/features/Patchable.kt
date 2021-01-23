package slatekit.data.features

import slatekit.common.data.Compare
import slatekit.common.data.Value
import slatekit.common.data.Filter
import slatekit.common.data.Logical

/**
 * Support patching of records by conditions
 */
interface Patchable<TId, T> : Inspectable<TId, T> where TId : Comparable<TId>, T:Any {

    /**
     * patches the items with the field and value supplied
     */
    fun patchByField(field: String, value: Any?): Int = patchByFilters(listOf(Value(field, value)), listOf(), Logical.And)


    /**
     * Patch 1 item by its id using the updates provided
     */
    fun patchById(id: TId, updates: List<Value>): Int = patchByFilters(updates, listOf(Filter(meta.pkey.name, Compare.Eq, id)), Logical.And)


    /**
     * Patch all items with the matching conditions
     * @param filters: The list of filters "id = 2" e.g. listOf( Filter("id", Op.Eq, "2" )
     * @param logical: The logical operator to use  e.g. "And | Or"
     */
    fun patchByFilters(fields:List<Value>, filters: List<Filter>, logical: Logical): Int
}
