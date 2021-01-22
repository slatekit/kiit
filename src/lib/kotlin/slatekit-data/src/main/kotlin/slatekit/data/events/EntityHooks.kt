package slatekit.data.events



interface EntityHooks<TId, T> where TId : Comparable<TId> {
    fun onEntityEvent(event: EntityEvent<TId, T>)
}
