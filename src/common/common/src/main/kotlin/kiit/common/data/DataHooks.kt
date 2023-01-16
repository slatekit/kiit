package kiit.common.data


/**
 * Hook for data changes.
 */
interface DataHooks<TId, T> where TId : Comparable<TId> {
    fun onDataEvent(event: DataEvent<TId, T>)
}
