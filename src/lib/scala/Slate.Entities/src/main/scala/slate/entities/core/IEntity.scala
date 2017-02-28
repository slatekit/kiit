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


trait Entity {

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
  def identity():Long


  /**
    * whether or not this entity is persisted.
    * @return
    */
  def isPersisted():Boolean = identity() > 0
}



/**
  * Base entity trait that must define if it is persisted or not
  * This is the recommended approach.
  */
trait EntityWithId extends Entity {

  /**
   * currently standardized to id of type long ( primary key, auto-inc )
   */
  val id: Long


  /**
    * provide a consistent approach to getting the identity for different
    * implementations of domain entities ( via either case class or non-case class )
    * @return
    */
  override def identity():Long = id
}



/**
  * Base entity trait that must define if it is persisted or not
  * NOTE: By default, immutable case classes serving as Domain models
  * are supported ( in which case they implement IEntityWithId ) with an immutable id
  * However, this approach is also supported to also client code to implement
  * domain classes any way the want except for getting/settings the id.
  */
trait EntityWithSetId[T] extends Entity {

  /**
    * currently standardized to id of type long ( primary key, auto-inc )
    */
  def getId():Long



  def setId(id:Long):T


  /**
    * provide a consistent approach to getting the identity for different
    * implementations of domain entities ( via either case class or non-case class )
    * @return
    */
  override def identity():Long = getId()
}


/**
 * Trait for entities that can be updatable
 * e.g. case class copying which must be implemented in the case class
 * @tparam T
 */
trait EntityUpdatable[T] {

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
trait EntityWithTime {
  val createdAt:DateTime
  val updatedAt:DateTime
}


/**
  * Entity with support for create/update user id
  */
trait EntityWithUser {
  val createdBy:Long
  val updatedBy:Long
}


/**
  * Entity with support for a unique id ( GUID )
  */
trait EntityWithGuid {
  val uniqueId: String
}


/**
 * Entity with support for both create/update timestamps and create/update user id
 */
trait EntityWithMeta
  extends EntityWithTime
  with    EntityWithUser
  with    EntityWithGuid
{
}
