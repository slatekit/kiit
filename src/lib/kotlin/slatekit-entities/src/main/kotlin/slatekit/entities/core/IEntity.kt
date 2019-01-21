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

package slatekit.entities.core

import slatekit.common.DateTime
import slatekit.common.serialization.Serializer
import slatekit.meta.Serialization
import slatekit.meta.models.Model

interface Entity {

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
    fun identity(): Long

    /**
     * whether or not this entity is persisted.
     * @return
     */
    fun isPersisted(): Boolean = identity() > 0
}

/**
 * Base entity interface that must define if it is persisted or not
 * This is the recommended approach.
 */
interface EntityWithId : Entity {

    /**
     * currently standardized to id of type long ( primary key, auto-inc )
     */
    val id: Long

    /**
     * provide a consistent approach to getting the identity for different
     * implementations of domain entities ( via either case class or non-case class )
     * @return
     */
    override fun identity(): Long = id
}

/**
 * interface for entities that can be updatable
 * e.g. case class copying which must be implemented in the case class
 * @tparam T
 */
interface EntityUpdatable<T> {

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    fun withId(id: Long): T
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
    val tag  : String
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
interface EntityWithMeta
    : EntityWithTime, EntityWithUser, EntityWithUUID

interface EntityModel {

    /**
     * Gets this entity as a generic Model schema, which holds all
     * the fields defined in the model for persistence.
     */
    val model: Model

    /**
     * Serializes this entity to json
     * {
     *   "name" : "user1",
     *   "email": "user1@abc.com"
     * }
     */
    fun toJson(): String = serialize(Serialization.json())

    /**
     * Serializes this entity to a csv record
     * name   ,  email
     * "user1",  user1@abc.com
     */
    fun toCsv(): String = serialize(Serialization.csv())

    /**
     * Serializes this entity to a props structure
     * name : user1
     * email: user1@abc.com
     */
    fun toProps(): String = serialize(Serialization.props())

    /**
     * Serializes this entity using the specific serializer supplied.
     */
    fun serialize(serializer: Serializer): String = serializer.serialize(this)
}
