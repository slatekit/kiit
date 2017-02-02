/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
  */

package slate.entities.core

import slate.common.DateTime



/**
  * Base entity trait that must define if it is persisted or not
  */
trait IEntity {

  /**
   * currently standardized to id of type long ( primary key, auto-inc )
   */
  val id: Long


  /**
   * whether or not this entity is persisted.
   * @return
   */
  def isPersisted(): Boolean = id > 0
}


/**
 * Trait for entities that can be updatable
 * e.g. case class copying which must be implemented in the case class
 * @tparam T
 */
trait IEntityUpdatable[T] {

  /**
   * sets the id on the entity and returns the entity with updated id.
   * @param id
   * @return
   */
  def withId(id:Long): T = ???
}


/**
  * Entity with support for create/update timestamps
  */
trait IEntityWithTime {
  val createdAt:DateTime
  val updatedAt:DateTime
}


/**
  * Entity with support for create/update user id
  */
trait IEntityWithUser {
  val createdBy:Long
  val updatedBy:Long
}


/**
  * Entity with support for a unique id ( GUID )
  */
trait IEntityWithGuid {
  val uniqueId: String
}


/**
 * Entity with support for both create/update timestamps and create/update user id
 */
trait IEntityWithMeta
  extends IEntityWithTime
  with    IEntityWithUser
  with    IEntityWithGuid
{
}
