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

package slate.entities.core

import slate.common.Model
import slate.common.mapper.Mapper
import slate.common.query.IQuery


import scala.reflect.runtime.universe.{typeOf,Type}

/**
  * Base Entity repository using generics with support for all the CRUD methods.
  * NOTE: This is basically a GenericRepository implementation
  * @param entityType   : The data type of the entity/model
  * @param entityIdType : The data type of the primary key/identity field
  * @param entityMapper : The entity mapper that maps to/from entities / records
  * @param nameOfTable  : The name of the table ( defaults to entity name )
  * @tparam T
  */
abstract class EntityRepo[T >: Null <: Entity] (
                                                  entityType  :Type                       ,
                                                  entityIdType:Option[Type]         = None,
                                                  entityMapper:Option[EntityMapper] = None,
                                                  nameOfTable :Option[String]       = None
                                                )
  extends IEntityRepo
{
  protected val _entityType  :Type         = entityType
  protected val _entityIdType:Type         = entityIdType.getOrElse(typeOf[Long])
  protected val _entityModel :Model        = entityMapper.fold(Mapper.loadSchema(entityType))(em => em.model())
  protected val _entityMapper:EntityMapper = entityMapper.getOrElse(new EntityMapper(_entityModel))

  /**
    * The name of the table in the datastore
    */
  def tableName():String =  nameOfTable.getOrElse(_entityType.typeSymbol.name.toString)


  /**
    * gets the internal mapper used to convert entities to sql or records to entity
    * @return
    */
  override def mapper():EntityMapper = _entityMapper


  /**
   * the name of the id field.
   * @return
   */
  def idName():String = _entityModel.idField.fold("id")( f => f.name)


  /**
    * creates the entity in the datastore
    * @param entity
    * @return
    */
  def create(entity: T):Long


  /**
    * updates the entity in the datastore
    * @param entity
    * @return
    */
  def update(entity: T): T


  /**
    * deletes the entity in the datastore
    * @param id
    * @return
    */
  def delete(id: Long): Boolean


  /**
    * deletes the entity in memory
    *
    * @param entity
    */
  def delete(entity:Option[T]): Boolean =
  {
    entity.fold(false)( en => this.delete(en.identity()) )
  }


  /**
    * gets the entity from the datastore using the id
    * @param id
    * @return
    */
  def get(id: Long) : Option[T]


  /**
    * gets all the entities from the datastore.
    * @return
    */
  def getAll() : List[T]


  /**
    * gets the total number of entities in the datastore
    * @return
    */
  def count() : Long


  /**
    * gets the top count entities in the datastore sorted by asc order
    * @param count: Top / Limit count of entities
    * @param desc : Whether to sort by descending
    * @return
    */
  def top(count:Int, desc:Boolean ): List[T]


  /**
    * determines if there are any entities in the datastore
    * @return
    */
  def any(): Boolean =  count() > 0


  /**
    * saves an entity by either creating it or updating it based on
    * checking its persisted flag.
    * @param entity
    */
  def save(entity: Option[T]): Unit =
  {
    entity.map( item => {
      if (item.isPersisted())
        update(item)
      else
        create(item)
    })
  }


  /**
    * saves all the entities
    *
    * @param items
    */
  def saveAll(items: Seq[T]) = items.foreach(item => save(Some(item)))


  /**
    * Gets the first/oldest item
    * @return
    */
  def first() : Option[T] = takeFirst( () => oldest(1))


  /**
    * Gets the last/recent item
    * @return
    */
  def last() : Option[T]  = takeFirst( () => recent(1))


  /**
    * Gets the most recent n items represented by count
    * @param count
    * @return
    */
  def recent(count:Int): List[T]  = top(count, true)


  /**
    * Gets the most oldest n items represented by count
    * @param count
    * @return
    */
  def oldest(count:Int): List[T]  = top(count, false)


  /**
    * takes the
    * @param call
    * @return
    */
  def takeFirst(call:() => List[T]): Option[T] =
  {
    val results = Option(call())
    results.flatMap( r => r.headOption)
  }


  /**
    * finds items based on the query
    * @param query
    * @return
    */
  def find(query:IQuery):List[T] = List[T]()


  /**
    * Hook for derived classes to handle additional logic before saving
    * @param entity
    */
  protected def onBeforeSave(entity: T) = {}
}
