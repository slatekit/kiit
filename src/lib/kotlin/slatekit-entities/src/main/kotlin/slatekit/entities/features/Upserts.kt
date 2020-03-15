package slatekit.entities.features

import slatekit.entities.Entity
import slatekit.results.Try
import slatekit.results.builders.Tries

interface Upserts<TId, T> :
    Creates<TId, T>,
    Updates<TId, T>,
    UniqueIds<TId, T> where TId : kotlin.Comparable<TId>, TId : kotlin.Number, T : Entity<TId> {


    fun upsertById(item: T, uuid: String): Try<T> {
        return try {
            when (item.isPersisted()) {
                false -> {
                    val id = this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                true -> {
                    val updated = this.update(item)
                    Tries.of(updated, item)
                }
            }
        } catch( ex:Exception ) {
            Tries.unexpected(ex)
        }
    }


    fun upsertByUUID(item: T, uuid: String): Try<T> {
        return try {
            val existing: T? = this.getByUUID(uuid)
            when (existing) {
                null -> {
                    val id = this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                else -> {
                    val updated = this.update(item)
                    Tries.of(updated, item)
                }
            }
        } catch (ex:Exception) {
            Tries.unexpected(ex)
        }
    }


    fun upsertByUUID(item: T, uuid: String, insertOp:((T) -> TId)? = null, updateOp:((T) -> T)? = null): Try<T> {
        return try {
            val existing: T? = this.getByUUID(uuid)
            when (existing) {
                null -> {
                    val id = this.create(item)
                    Tries.of(id.toLong() > 0L, item)
                }
                else -> {
                    val updated = this.update(item)
                    Tries.of(updated, item)
                }
            }
        } catch (ex:Exception) {
            Tries.unexpected(ex)
        }
    }
}
