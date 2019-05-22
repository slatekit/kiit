package slatekit.entities.slatekit.entities.features

import slatekit.entities.Entity
import slatekit.results.Try

/**
 * Dynamic CRUD Support without knowning what type of Entity[T] it is.
 * This just requires knowing the Id type
 */
interface EntityDynamic<TId> where TId:Comparable<TId> {


    fun createEntity(entity: Entity<TId>): Try<TId>


    fun updateEntity(entity: Entity<TId>): Try<TId>


    fun fetchEntity(entity: Entity<TId>): Try<TId>


    fun deleteEntity(entity: Entity<TId>): Try<TId>
}