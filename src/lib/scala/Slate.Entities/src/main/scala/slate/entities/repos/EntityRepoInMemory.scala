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
import slate.entities.core.{IEntityUpdatable, EntityMapper, EntityRepo, IEntity}

import scala.reflect.runtime.universe.Type


class EntityRepoInMemory[T >: Null <: IEntity](
                                                 entityType  :Type,
                                                 entityIdType:Option[Type]         = None,
                                                 entityMapper:Option[EntityMapper] = None
                                               )
  extends EntityRepo[T](entityType, entityIdType, entityMapper, None)
{

  private val _items = new ListMap[Long,T]()


  /**
   * create the entity in memory
    *
    * @param entity
   */
  override def create(entity: T):Long =
  {
    // Check 1: already persisted ?
    if(!entity.isPersisted()) {
      // get next id
      val id = getNextId()
      val en = entity match {
        case w:IEntityUpdatable[T] => w.withId(id)
        case _                     => _entityMapper.copyWithId[T](id, entity)
      }

      // store
      _items.add(id, en)
      id
    }
    else
      entity.id
  }


  /**
   * updates the entity in memory
    *
    * @param entity
   */
  override def update(entity: T) :T =
  {
    // Check 1: already persisted ?
    if(entity.isPersisted()) {
      _items(entity.id) = entity
    }
    entity
  }


  /**
    * deletes the entity in memory
    *
    * @param id
    */
  override def delete(id: Long): Boolean =
  {
    if(!_items.contains(id))
      false
    else {
      _items.remove(id)
      true
    }
  }


  /**
   * gets the entity from memory with the specified id.
    *
    * @param id
   */
  override def get(id: Long): Option[T] =
  {
    if(_items.contains(id)) Some(_items(id)) else None
  }


  /**
    * gets all the items from memory
    *
    * @return
    */
  override def getAll() : List[T] = _items.all()


  override def count() : Long  = _items.size


  override def top(count:Int, desc:Boolean ): List[T]  =
  {
    if(_items.size() == 0) {
      List[T]()
    }
    else {
      val items = _items.all.sortBy(item => item.id)
      val sorted = if (desc) items.reverse else items
      sorted.take(count)
    }
  }


  private def getNextId(): Long = _items.size() + 1
}
