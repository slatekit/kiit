package slatekit.data.features


import slatekit.query.Select
import slatekit.query.Delete
import slatekit.query.Update

interface Criteria<TId, T> where TId : Comparable<TId> {
    fun delete(): Delete
    fun select(): Select
    fun patch(): Update
}
