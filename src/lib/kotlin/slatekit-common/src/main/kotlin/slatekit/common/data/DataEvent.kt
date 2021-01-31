package slatekit.common.data

import slatekit.common.DateTime

/**
 * Used to
 */
sealed class DataEvent<TId, T> where TId : Comparable<TId> {
    data class DataCreated<TId, T>(val name:String, val id: TId, val entity: T, val timestamp: DateTime) : DataEvent<TId, T>()  where TId : Comparable<TId>
    data class DataUpdated<TId, T>(val name:String, val id: TId, val entity: T, val timestamp: DateTime) : DataEvent<TId, T>()  where TId : Comparable<TId>
    data class DataDeleted<TId, T>(val name:String, val id: TId, val entity: T?, val timestamp: DateTime) : DataEvent<TId, T>()  where TId : Comparable<TId>
    data class DataErrored<TId, T>(val name:String, val id: TId, val entity: T?, val exception: Exception, val timestamp: DateTime) : DataEvent<TId, T>() where TId : Comparable<TId>
}
