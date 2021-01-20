package slatekit.data.repos

import slatekit.data.features.Identifiable

import slatekit.data.Consts
import slatekit.data.core.EntityInfo

/**
 * Repository
 */
interface Repo<TId, T> : Identifiable<TId> where TId : Comparable<TId> {
    /**
     * Used for metadata and to get id/persisted state
     */
    val info: EntityInfo

    /**
     * the name of the id field.
     * @return
     */
    override fun id(): String = info.model?.idField?.name ?: Consts.idCol

    /**
     * The name of the table in the datastore
     */
    override fun name(): String = info.name()

    /**
     * Check if entity is persisted ( has an id ) in the database
     */
    fun isPersisted(entity: T): Boolean {
        return info.idInfo.isPersisted(entity)
    }

    /**
     * Get identity of entity
     */
    fun identity(entity: T): TId {
        return info.idInfo.identity(entity)
    }
}
