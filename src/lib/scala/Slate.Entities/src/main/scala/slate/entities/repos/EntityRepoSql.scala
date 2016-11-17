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

import slate.common.databases.Db
import slate.common.query.IQuery
import slate.entities.core.{EntityRepo, IEntity}

import scala.reflect.runtime.universe.Type


abstract class EntityRepoSql [T >: Null <: IEntity ](entityType:Type)
    extends EntityRepo[T](entityType) {

  protected var _db:Db = null


  def setDb(db:Db):Unit = {
    _db = db
  }


  override def create(entity: T):Long =
  {
    val sql = mapFields(entity, false)
    val id = _db.executeInsertGetId(s"insert into ${tableName} " + sql + ";")
    entity.id = id
    id
  }


  override def update(entity: T):Unit =
  {
    val sql = mapFields(entity, true)
    val id = entity.id
    sqlExecute(s"update ${tableName} set " + sql + s" where Id = ${id};")
  }


  /**
    * deletes the entity in memory
    *
    * @param id
    */
  override def delete(id: Long): Boolean =
  {
    val count = sqlExecute(s"delete from ${tableName} where Id = ${id};")
    count > 0
  }


  def get(id: Long) : Option[T] =
  {
    sqlMapOne(s"select * from ${tableName} where Id = ${id};")
  }


  override def getAll() : List[T] =
  {
    val result = sqlMapMany(s"select * from ${tableName};")
    result.getOrElse( List[T]() )
  }


  override def count() : Long  =
  {
    val count = _db.getScalarLong(s"select count(*) from ${tableName};" )
    count
  }


  override def find(query:IQuery):List[T] =
  {
    val filter = query.toFilter()
    val sql = s"select * from ${tableName} where " + filter
    val results = sqlMapMany(sql)
    results.getOrElse( List[T]() )
  }


  protected def scriptLastId(): String =
  {
    ""
  }


  private def sqlExecute(sql: String) :Int =
  {
    _db.executeUpdate(sql)
  }


  private def sqlMapMany(sql: String) :Option[List[T]] =
  {
    _db.mapMany[T](sql, _mapper)
  }


  private def sqlMapOne(sql: String) :Option[T] =
  {
    _db.mapOne[T](sql, _mapper)
  }


  private def mapFields(item: IEntity, isUpdate: Boolean) : String =
  {
    _mapper.mapToSql(item, isUpdate, false)
  }
}
