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

import slate.common._
import slate.common.encrypt.Encryptor
import slate.common.i18n.I18nStrings
import slate.common.logging.LoggerBase
import slate.common.query.IQuery


/**
  * Base entity service with generics to support all CRUD operations.
  * Delegates calls to the entity repository, and also manages the timestamps
  * on the entities for create/update operations
  * @tparam T
  */
class EntityService[T >: Null <: Entity](protected val _repo:EntityRepo[T])
  extends IEntityService
{

  /**
    * gets the repo representing the underlying datastore
    * @return
    */
  def repo():IEntityRepo = _repo


  /**
    * creates the entity in the datastore
    * @param entity
    * @return
    */
  def create(entity: T) :Long =
  {
    val finalEntity = applyFieldData(1, entity)
    _repo.create(finalEntity)
  }


  /**
    * updates the entity in the datastore
    * @param entity
    * @return
    */
  def update(entity: T) :Unit =
  {
    val finalEntity = applyFieldData(2, entity)
    _repo.update(finalEntity)
  }


  /**
    * updates the entity field in the datastore
    * @param id: id of the entity
    * @param field: the name of the field
    * @param value: the value to set on the field
    * @return
    */
  def update(id:Long, field:String, value:String): Unit =
  {
    val item = get(id)
    item.map( entity => {
      Reflector.setFieldValue(entity, field, value)
      update(entity)
      Unit
    })
  }


  /**
    * deletes the entity in the datastore
    * @param id
    * @return
    */
  def delete(id: Long) :Boolean =
  {
    _repo.delete(id)
  }


  /**
    * deletes the entity in memory
    *
    * @param entity
    */
  def delete(entity:Option[T]) :Unit =
  {
    _repo.delete(entity)
  }


  /**
    * gets the entity from the datastore using the id
    * @param id
    * @return
    */
  def get(id: Long):Option[T] =
  {
    _repo.get(id)
  }


  /**
    * gets all the entities from the datastore.
    * @return
    */
  def getAll():List[T] =
  {
    _repo.getAll()
  }


  /**
    * gets the total number of entities in the datastore
    * @return
    */
  def count(): Long =
  {
    _repo.count()
  }


  /**
    * gets the top count entities in the datastore sorted by asc order
    * @param count: Top / Limit count of entities
    * @param desc : Whether to sort by descending
    * @return
    */
  def top(count:Int, desc:Boolean ): List[T] =
  {
    _repo.top(count, desc)
  }


  /**
    * determines if there are any entities in the datastore
    * @return
    */
  def any(): Boolean =
  {
    _repo.any()
  }


  /**
    * saves an entity by either creating it or updating it based on
    * checking its persisted flag.
    * @param entity
    */
  def save(entity: Option[T]):Unit =
  {
    entity.map( e => {
      val finalEntity = applyFieldData(3, e)
      _repo.save(Option(finalEntity))
      Unit
    })
  }


  /**
    * saves all the entities
    *
    * @param items
    */
  def saveAll(items: Seq[T]) =
  {
    _repo.saveAll(items)
  }


  /**
    * Gets the first/oldest item
    * @return
    */
  def first() : Option[T] =
  {
    _repo.first()
  }


  /**
    * Gets the last/recent item
    * @return
    */
  def last() : Option[T]  =
  {
    _repo.last()
  }


  /**
    * Gets the most recent n items represented by count
    * @param count
    * @return
    */
  def recent(count:Int): List[T]  =
  {
    _repo.recent(count)
  }


  /**
    * Gets the most oldest n items represented by count
    * @param count
    * @return
    */
  def oldest(count:Int): List[T]  =
  {
    _repo.oldest(count)
  }


  /**
    * Gets distinct items by the field name ( used for derived sql implementations )
    * @param field
    * @tparam Ty
    * @return
    */
  def distinct[Ty](field:String):List[Ty] =
  {
    List[Ty]()
  }


  /**
    * finds items based on the query
    * @param query
    * @return
    */
  def find(query:IQuery):List[T] =
  {
    _repo.find(query)
  }


  def findFirst(query:IQuery) : Option[T] =
  {
    val results = find(query)
    val any = Option(results).fold(false)( r => r.nonEmpty)
    if(any)
      results(0).asInstanceOf[Option[T]]
    else
      None
  }


  /**
    * Hook for derived classes to handle additional logic before saving
    * @param entity
    */
  protected def onBeforeSave(entity: T) = {
  }


  /**
    * Hook for derived to apply any other logic/field changes before create/update
    * @param mode
    * @param entity
    * @return
    */
  protected def applyFieldData(mode:Int, entity:T ) : T =
  {
    entity
  }
}

