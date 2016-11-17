/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.entities.repos

import slate.common.ListMap
import slate.entities.core.{EntityRepo, IEntity}

import scala.reflect.runtime.universe.Type


class EntityRepoInMemory[T >: Null <: IEntity ](entityType:Type)
  extends EntityRepo[T](entityType:Type)
{

  private var _id = 0L
  private val _items = new ListMap[Long,T]()


  /**
   * create the entity in memory
    *
    * @param entity
   */
  override def create(entity: T):Long =
  {
    // Check 1: already persisted ?
    if(entity.isPersisted())
      return entity.id

    // get next id
    entity.id = getNextId()

    // store
    _items.add(entity.id, entity)

    entity.id
  }


  /**
   * updates the entity in memory
    *
    * @param entity
   */
  override def update(entity: T) :Unit =
  {
    // Check 1: already persisted ?
    if(!entity.isPersisted())
      return

    // store
    _items(entity.id) = entity
  }


  /**
    * deletes the entity in memory
    *
    * @param id
    */
  override def delete(id: Long): Boolean =
  {
    if(!_items.contains(id))
      return false

    _items.remove(id)
    true
  }


  /**
   * gets the entity from memory with the specified id.
    *
    * @param id
   */
  override def get(id: Long): Option[T] =
  {
    if(_items.contains(id))
      return Some(_items(id))

    None
  }


  /**
    * gets all the items from memory
    *
    * @return
    */
  override def getAll() : List[T] =
  {
    _items.all()
  }


  override def count() : Long  =
  {
    _items.size
  }


  override def top(count:Int, desc:Boolean ): List[T]  =
  {
    if(_items.size() == 0)
      return List[T]()

    var sorted = _items.all.sortBy( item => item.id )
    if(desc)
      sorted = sorted.reverse

    sorted.take(count)
  }


  private def getNextId(): Long =
  {
    _id = _id + 1
    _id
  }
}
