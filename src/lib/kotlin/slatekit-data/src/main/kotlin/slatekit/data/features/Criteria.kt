package slatekit.data.features

import slatekit.query.Select
import slatekit.query.Update
import slatekit.query.Delete

interface Criteria<TId, T> where TId : Comparable<TId> {
    fun delete(): Delete
    fun select(): Select
    fun update(): Update
}
