package slatekit.entities.features

import slatekit.entities.Entity
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Upserts<TId, T> :
    Creates<TId, T>,
    Updates<TId, T>,
    UniqueIds<TId, T> where TId : kotlin.Comparable<TId>, TId : kotlin.Number, T : Entity<TId> {


    /**
     * Upserts an item by checking if it exists already using it identity
     */
    suspend fun upsertById(item: T, uuid: String): Try<T> {
        return upsertById(item, uuid, { newItem -> this.create(newItem) }, { oldItem -> this.update(oldItem) })
    }


    /**
     * Upserts an item by checking if it exists already using it identity and calls the create or update
     * operations supplied. If they are null, defaults to using the existing create/update methods
     */
    suspend fun upsertById(item: T, uuid: String, createOp:(suspend (T) -> TId)? = null, updateOp:(suspend (T) -> Boolean)? = null): Try<T> {
        return try {
            when (item.isPersisted()) {
                false -> {
                    val id = createOp?.let { it(item) }  ?: this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                true -> {
                    val updated = updateOp?.let { it(item) } ?: this.update(item)
                    Tries.of(updated, item)
                }
            }
        } catch( ex:Exception ) {
            Tries.unexpected(ex)
        }
    }


    /**
     * Upserts an item by checking if it exists already using it uuid
     */
    suspend fun upsertByUUID(item: T, uuid: String): Try<T> {
        return upsertByUUID(item, uuid, { newItem -> this.create(newItem) }, { oldItem -> this.update(oldItem) })
    }


    /**
     * Upserts an item by checking if it exists already using it uuid and calls the create or update
     * operations supplied. If they are null, defaults to using the existing create/update methods
     */
    suspend fun upsertByUUID(item: T, uuid: String, createOp:(suspend (T) -> TId)? = null, updateOp:(suspend (T) -> Boolean)? = null): Try<T> {
        return try {
            val existing: T? = this.getByUUID(uuid)
            when (existing) {
                null -> {
                    val id = createOp?.let { it(item) }  ?: this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                else -> {
                    val updated = updateOp?.let { it(item) } ?: this.update(item)
                    Tries.of(updated, item)
                }
            }
        } catch (ex:Exception) {
            Tries.unexpected(ex)
        }
    }


    /**
     * Creates an item only if it does NOT exist already by checking its uuid
     */
    suspend fun createByUUID(item: T, uuid: String, createOp:((T) -> TId)? = null): Try<T> {
        return try {
            val existing: T? = this.getByUUID(uuid)
            when (existing) {
                null -> {
                    val id = createOp?.let { it(item) }  ?: this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                else -> {
                    Tries.success(item)
                }
            }
        } catch (ex:Exception) {
            Tries.unexpected(ex)
        }
    }
}
