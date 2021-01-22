package slatekit.data.events

import slatekit.common.DateTime

/**
 * Used to
 */
sealed class EntityEvent<TId, T> where TId : Comparable<TId> {
    data class EntityCreated<TId, T>(val name:String, val id: TId, val entity: T, val timestamp: DateTime) : EntityEvent<TId, T>()  where TId : Comparable<TId>
    data class EntityUpdated<TId, T>(val name:String, val id: TId, val entity: T, val timestamp: DateTime) : EntityEvent<TId, T>()  where TId : Comparable<TId>
    data class EntityDeleted<TId, T>(val name:String, val id: TId, val entity: T?, val timestamp: DateTime) : EntityEvent<TId, T>()  where TId : Comparable<TId>
    data class EntityErrored<TId, T>(val name:String, val id: TId, val entity: T?, val exception: Exception, val timestamp: DateTime) : EntityEvent<TId, T>() where TId : Comparable<TId>
}
