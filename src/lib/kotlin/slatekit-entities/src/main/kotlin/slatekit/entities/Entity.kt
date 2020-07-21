/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.entities

import slatekit.common.DateTime

interface Entity<TId : Comparable<TId>> {

    /**
     * gets the id
     *
     * NOTES:
     * 1. This is a method instead of a val "id" to allow domain entities more
     *    flexibility in how to implement their own identity.
     * 2. This also allows for 2 default implementations ( EntityWithId and EntityWithSetId )
     *
     * @return
     */
    fun identity(): TId

    /**
     * whether or not this entity is persisted.
     * @return
     */
    fun isPersisted(): Boolean
}

/**
 * Base entity interface that must define if it is persisted or not
 * This is the recommended approach.
 */
interface EntityWithId<TId : Comparable<TId>> : Entity<TId> {

    /**
     * currently standardized to id of type long ( primary key, auto-inc )
     */
    val id: TId

    override fun identity(): TId = id
}

/**
 * interface for entities that can be updatable
 * e.g. case class copying which must be implemented in the case class
 * @tparam T
 */
interface EntityUpdatable<TId, T> where TId : Comparable<TId>, T : Entity<TId> {

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    fun withId(id: TId): T

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    fun withIdAny(id: Any): T = withId(id as TId)
}

/**
 * Entity with support for create/update timestamps
 */
interface EntityWithTime {
    val createdAt: DateTime
    val updatedAt: DateTime
}

/**
 * Entity with support for create/update user id
 * NOTE: This ids can be either long/UUID, so making this type a string to support either
 */
interface EntityWithUser {
    val createdBy: String
    val updatedBy: String
}

/**
 * Entity with support for a unique id ( GUID )
 */
interface EntityWithUUID {
    val uuid: String
}

/**
 * interface for entities that can be updatable
 * e.g. case class copying which must be implemented in the case class
 * @tparam T
 */
interface EntityWithUUIDUpdatable<TId, T> where TId : Comparable<TId>, T : Entity<TId> {

    /**
     * sets the uuid on the entity and returns the entity with updated uuid.
     * @param uuid
     * @return
     */
    fun withUUID(uuid: String): T
}

/**
 * Entity with support for sorting
 */
interface EntityWithOrdinal {
    val ordinal: Short
}

/**
 * Entity with support for both create/update timestamps and create/update user id
 */
interface EntityWithShard {
    val shard: String
}

/**
 * Entity with support for both create/update timestamps and create/update user id
 */
interface EntityWithLabel {
    val label: String
}

/**
 * Entity with support for both create/update timestamps and create/update user id
 */
interface EntityWithCorrelation {
    val xid: String
}

/**
 * Entity with support for both create/update timestamps and create/update user id
 */
interface EntityWithTags {
    val tags: String
}

/**
 * Entity with support for both create/update timestamps and create/update user id
 */
interface EntityWithMeta :
    EntityWithTime, EntityWithUser, EntityWithUUID
