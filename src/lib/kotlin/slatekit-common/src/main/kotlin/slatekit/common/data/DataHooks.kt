package slatekit.common.data


interface DataHooks<TId, T> where TId : Comparable<TId> {
    fun onDataEvent(event: DataEvent<TId, T>)
}
