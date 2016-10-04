/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.entities.core

import slate.common.query.IQuery


import scala.reflect.runtime.universe.Type

/**
  * Base Entity repository using generics with support for all the CRUD methods.
  * @param _entityType : The type of the entity
  * @tparam T
  */
abstract class EntityRepo[T >: Null <: IEntity] (val _entityType:Type)
  extends IEntityRepo
{

  protected val _tableName =  if(_entityType != null) _entityType.typeSymbol.name
  protected var _mapper:EntityMapper = null


  def setMapper(mapper:EntityMapper): Unit =
  {
    _mapper = mapper
  }


  override def mapper():EntityMapper =
  {
    _mapper
  }


  def create(entity: T):Long


  def update(entity: T): Unit


  def delete(id: Long): Boolean


  def get(id: Long) : Option[T]


  def getAll() : List[T]


  def count() : Long


  def top(count:Int, desc:Boolean ): List[T]



  def any(): Boolean =
  {
    count() > 0
  }


  /**
    * deletes the entity in memory
    *
    * @param entity
    */
  def delete(entity:Option[T]): Boolean =
  {
    if(entity.isEmpty)
      return false
    delete(entity.get.id)
  }


  /**
    * saves an entity
    *
    * @param entity
    */
  def save(entity: Option[T]): Unit =
  {
    if(entity.isEmpty)
      return

    val item = entity.get
    if(item.isPersisted())
      update(item)
    else
      create(item)
  }


  /**
    * saves all the entities
    *
    * @param items
    */
  def saveAll(items: Seq[T]) =
  {
    for(item <- items )
    {
      save(Some(item))
    }
  }


  def first() : Option[T] =
  {
    takeFirst( () => oldest(1))
  }


  def last() : Option[T]  =
  {
    takeFirst( () => recent(1))
  }


  def recent(count:Int): List[T]  =
  {
    top(count, true)
  }


  def oldest(count:Int): List[T]  =
  {
    top(count, false)
  }


  def takeFirst(call:() => List[T]): Option[T] =
  {
    val results = call()
    if (results == null || results.isEmpty)
      return None
    Some(results.head)
  }


  def find(query:IQuery):List[T] =
  {
    List[T]()
  }


  protected def onBeforeSave(entity: T) =
  {

  }


  protected def tableName:String =
  {
    _tableName.toString
  }
}
