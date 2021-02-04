package slatekit.data.features

import slatekit.query.Select
import slatekit.query.Update
import slatekit.query.Where

interface Criteria<TId, T> where TId : Comparable<TId> {
    fun delete(): Where
    fun select(): Select
    fun update(): Update
}
