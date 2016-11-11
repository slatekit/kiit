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

import slate.common._
import slate.common.encrypt.Encryptor
import slate.common.query.IQuery


/**
  * Base entity service with generics to support all CRUD operations.
  * Delegates calls to the entity repository, and also manages the timestamps
  * on the entities for create/update operations
  * @tparam T
  */
class EntityService[T >: Null <: IEntity]
  extends IEntityService
{
  var _repo: EntityRepo[T] = null

  var encryptor:Option[Encryptor] = None
  
  
  def this(repo:EntityRepo[T]) =
  {
    this()
    _repo = repo
  }


  def init(repo: EntityRepo[T]):Unit =
  {
    this._repo = repo
  }


  def repo():IEntityRepo =
  {
    _repo
  }


  def create(entity: T) :Long =
  {
    applyFieldData(1, Some(entity))
    _repo.create(entity)
  }


  def update(entity: T) :Unit =
  {
    applyFieldData(2, Some(entity))
    _repo.update(entity)
  }


  def update(id:Long, field:String, value:String): Unit =
  {
    val item = get(id)
    if(item.isEmpty) return
    val entity = item.get
    Reflector.setFieldValue(entity, field, value)
    update(entity)
  }


  def delete(id: Long) :Boolean =
  {
    _repo.delete(id)
  }


  def get(id: Long):Option[T] =
  {
    _repo.get(id)
  }


  def getAll():List[T] =
  {
    _repo.getAll()
  }


  def count(): Long =
  {
    _repo.count()
  }


  def top(count:Int, desc:Boolean ): List[T] =
  {
    _repo.top(count, desc)
  }


  def any(): Boolean =
  {
    _repo.any()
  }


  def delete(entity:Option[T]) :Unit =
  {
    _repo.delete(entity)
  }


  def save(entity: Option[T]) =
  {
    applyFieldData(3, entity)
    _repo.save(entity)
  }


  def saveAll(seq: Seq[T]) =
  {
    _repo.saveAll(seq)
  }


  def first() : Option[T] =
  {
    _repo.first()
  }


  def last() : Option[T]  =
  {
    _repo.last()
  }


  def recent(count:Int): List[T]  =
  {
    _repo.recent(count)
  }


  def oldest(count:Int): List[T]  =
  {
    _repo.oldest(count)
  }


  def distinct[Ty](name:String):List[Ty] =
  {
    List[Ty]()
  }


  def find(query:IQuery):List[T] =
  {
    _repo.find(query)
  }


  def findFirst(query:IQuery) : Option[T] =
  {
    val results = find(query)
    if(results == null || results.size == 0)
      return None
    results(0).asInstanceOf[Option[T]]
  }


  protected def onBeforeSave(entity: T) = {
  }


  def applyFieldData(mode:Int, entity:Option[T] )
  {
    if (!entity.isDefined)
      return

    val item = entity.get

    // 1. Time stamps
    if (mode == 1 || !item.isPersisted()) {
      item.createdAt = DateTime.now()
    }
    item.updatedAt = DateTime.now()

    // 2. Unique id ( GUID )
    if (item.isInstanceOf[IEntityUnique]){
      val unique = item.asInstanceOf[IEntityUnique]
      if (Strings.isNullOrEmpty(unique.uniqueId) ){
        unique.uniqueId = Random.stringGuid(false)
      }
    }
  }
}

