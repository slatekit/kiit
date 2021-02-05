package slatekit.data.features

import slatekit.common.data.Value
import slatekit.query.Op
import slatekit.query.Select
import slatekit.query.Update

/**
 * Support patching of records by conditions
 */
interface Patchable<TId, T> : Inspectable<TId, T> where TId : Comparable<TId>, T:Any {

    /**
     * patches the items with the field and value supplied
     */
    fun patchByField(field: String, value: Any?): Int = patchByQuery(patch().set(Value(columnName(field), meta.pkey.type, value)))


    /**
     * Patch 1 item by its id using the updates provided
     */
    fun patchById(id: TId, updates: List<Value>): Int = patchByQuery(patch().set(updates).where(meta.pkey.name, Op.Eq, id))


    /**
     * Patches using a query builder
     * patch { set("active", 2).where("id" 3) }
     */
    fun patch(builder: Update.() -> Unit): Int {
        val s = patch()
        builder(s)
        return patchByQuery(s)
    }


    /**
     * Patch using the query builder
     */
    fun patchByQuery(builder: Update): Int
}
