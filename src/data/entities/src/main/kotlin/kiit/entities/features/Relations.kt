package kiit.entities.features

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kiit.common.NOTE
import kiit.entities.Entities
import kiit.entities.Entity
import kiit.entities.core.EntityOps
import kiit.query.Op

interface Relations<TId, T> : EntityOps<TId, T> where TId : kotlin.Comparable<TId>, T : Entity<TId> {

    fun entities(): Entities

    /**
     * Gets a relation model associated w the current model by the property supplied.
     * E.g. get the membership and user associated with the membership: 1 membership = 1 user
     * @param id : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop : the field to check on current model for the foreign key id
     * @sample member.getRelation[User](1, Member::userId, User::class)
     */
    suspend fun <R> getRelation(id: TId, prop: KProperty<*>, model: KClass<*>): R? where R : Entity<TId> {

        NOTE.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity = repo().getById(id)
        return entity?.let { ent ->
            val id = prop.getter.call(entity) as TId
            val relRepo = entities().getRepo(model as KClass<R>)
            val rel = relRepo.getById(id)
            rel
        }
    }

    /**
     * Gets a relational model associated w the current model by the property supplied.
     * E.g. get the membership and user associated with the membership: 1 membership = 1 user
     * @param id : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop : the field to check on current model for the foreign key id
     * @sample member.getRelation[User](1, Member::userId, User::class)
     */
    suspend fun <R> getWithRelation(id: TId, prop: KProperty<*>, model: KClass<*>): Pair<T?, R?> where R : Entity<TId> {

        NOTE.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity = repo().getById(id)
        return entity?.let { ent ->
            val id = prop.getter.call(entity) as TId
            val relRepo = entities().getRepo(model as KClass<R>)
            val rel = relRepo.getById(id)
            Pair(entity, rel)
        } ?: Pair(null, null)
    }

    /**
     * Gets all the models associated w the current model by the property supplied.
     * E.g. get all the members in a group: 1 group = many members
     * @param id : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop : the field to check on current model for the foreign key id
     * @sample group.getWithRelations[Member](1, Member::class, Member::groupId)
     */
    suspend fun <R> getWithRelations(id: TId, model: KClass<*>, prop: KProperty<*>): Pair<T?, List<R>> where R : Entity<TId> {

        NOTE.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity = repo().getById(id)
        return entity?.let { ent ->
            val relRepo = entities().getRepo(model as KClass<R>)
            val relations = relRepo.findByField(prop.name, Op.Eq, id)
            Pair(entity, relations)
        } ?: Pair(null, listOf())
    }
}
