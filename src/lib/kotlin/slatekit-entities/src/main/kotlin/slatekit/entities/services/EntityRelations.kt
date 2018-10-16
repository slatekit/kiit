package slatekit.entities.services

import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface EntityRelations<T> : ServiceSupport<T> where T : Entity {

    /**
     * Gets a relation model associated w the current model by the property supplied.
     * E.g. get the membership and user associated with the membership: 1 membership = 1 user
     * @param id    : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop  : the field to check on current model for the foreign key id
     * @sample member.getRelation[User](1, Member::userId, User::class)
     */
    fun <R> getRelation(id: Long, prop: KProperty<*>, model: KClass<*>): R? where R : Entity{

        TODO.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity =  entityRepo().get(id)
        return entity?.let { ent ->
            val id = prop.getter.call(entity) as Long
            val relRepo = entities().getRepo<R>(model)
            val rel = relRepo.get(id)
            rel
        }
    }


    /**
     * Gets a relational model associated w the current model by the property supplied.
     * E.g. get the membership and user associated with the membership: 1 membership = 1 user
     * @param id    : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop  : the field to check on current model for the foreign key id
     * @sample member.getRelation[User](1, Member::userId, User::class)
     */
    fun <R> getWithRelation(id: Long, prop: KProperty<*>, model: KClass<*>): Pair<T?,R?> where R : Entity{

        TODO.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity =  entityRepo().get(id)
        return entity?.let { ent ->
            val id = prop.getter.call(entity) as Long
            val relRepo = entities().getRepo<R>(model)
            val rel = relRepo.get(id)
            Pair(entity, rel)
        } ?: Pair(null, null)
    }


    /**
     * Gets all the models associated w the current model by the property supplied.
     * E.g. get all the members in a group: 1 group = many members
     * @param id    : id of the current model
     * @param model : the target ( associated relationship ) model to get
     * @param prop  : the field to check on current model for the foreign key id
     * @sample group.getWithRelations[Member](1, Member::class, Member::groupId)
     */
    fun <R> getWithRelations(id: Long, model: KClass<*>, prop: KProperty<*>): Pair<T?,List<R>> where R : Entity{

        TODO.IMPROVE("entities", "This should ideally be in 1 database call")
        val entity =  entityRepo().get(id)
        return entity?.let { ent ->
            val relRepo = entities().getRepo<R>(model)
            val relations = relRepo.findBy(prop.name, "=", id)
            Pair(entity, relations)
        } ?: Pair(null, listOf())
    }
}