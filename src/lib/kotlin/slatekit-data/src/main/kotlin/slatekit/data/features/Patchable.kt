package slatekit.data.features

import slatekit.common.data.Value
import slatekit.query.IQuery
import slatekit.query.Op
import slatekit.query.Query

/**
 * Support patching of records by conditions
 */
interface Patchable<TId, T> : Inspectable<TId, T> where TId : Comparable<TId>, T:Any {

    /**
     * patches the items with the field and value supplied
     */
    fun patchByField(field: String, value: Any?): Int = patchByQuery(Query().set(Value(columnName(field), meta.pkey.type, value)))


    /**
     * Patch 1 item by its id using the updates provided
     */
    fun patchById(id: TId, updates: List<Value>): Int = patchByQuery(Query().set(updates).where(meta.pkey.name, Op.Eq, id))


    /**
     * Patch using the query builder
     */
    fun patchByQuery(query: IQuery): Int
}
