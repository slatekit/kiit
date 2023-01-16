package kiit.data.features


import kiit.query.Select
import kiit.query.Delete
import kiit.query.Update

interface Criteria<TId, T> where TId : Comparable<TId> {
    fun delete(): Delete
    fun select(): Select
    fun patch(): Update
}
