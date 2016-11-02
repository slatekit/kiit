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

package slate.entities.models

import slate.common.app.AppMeta
import slate.common.databases.{DbConString, DbBuilder}
import slate.common.info.Folders
import slate.common.results.ResultSupportIn
import slate.common.{NoResult, Files, Result}
import slate.entities.core.{EntityInfo, Entities}

import scala.collection.mutable.ListBuffer

/**
  * Created by kreddy on 3/23/2016.
  */
class EntitySetupService(val _entities:Entities,
                         val _settings:ModelSettings,
                         val _folders:Option[Folders]) extends ResultSupportIn {

  def names(): List[String] = {
    val names = ListBuffer[String]()
    val all = _entities.getEntities()
    for( item <- all )
    {
      val name = item.entityType.get.typeSymbol.fullName
      names.append(name)
    }
    names.toList
  }


  /**
   * installs all the registered entities in the database
    *
    * @return
   */
  def installAll(): Result[String] =
  {
    eachEntity( (entity) =>
    {
      val name = entity.entityType.get.typeSymbol.fullName
      install(name, "1", entity.dbKey, entity.dbShard )
    })
  }


  /**
   * generates all the sql install files for all the registered entities
    *
    * @return
   */
  def generateSqlAll(): Result[String] =
  {
    eachEntity( (entity) =>
    {
      val name = entity.entityType.get.typeSymbol.fullName
      generateSql(name, "1")
    })
  }


  /**
   * installs the model name supplied into the database.
    *
    * @param name    : the fully qualified name of the model e..g slate.ext.resources.Resource
   * @param version : the version of the model
   * @param dbKey   : the dbKey pointing to the database to install the model to. leave empty to use default db
   * @param dbShard : the dbShard pointing to the database to install the model to. leave empty to use default db
   * @return
   */
  def install(name:String, version:String = "", dbKey:String = "", dbShard:String = ""): Result[Boolean] =
  {
    val result = generateSql(name, version)
    if(!result.success)
    {
      return failure(Some(s"Unable to install, can not generate sql for model ${name}"))
    }
    val sql = result.get
    val db = _entities.getDatabase(dbKey, dbShard)
    db.executeUpdate(sql)
    ok()
  }


  /**
   * generates the sql for installing the model, file is created in the .{appname}/apps/ directory.
    *
    * @param name    : the fully qualified name of the model e..g slate.ext.resources.Resource
   * @param version : the version of the model
   * @return
   */
  def generateSql(name:String, version:String = ""): Result[String] =
  {
    var sql = ""
    var success = true
    var message = ""
    try
    {
      val fullName = name
      val svc = _entities.getServiceByName( fullName )
      val model = svc.repo().mapper().model()
      sql = new DbBuilder().addTable(model)

      if ( _settings.enableOutput && _folders.isDefined)
      {
        val folders = _folders.get
        Files.writeDatedFile(folders.pathToOutputs, s"model-${model.name}.sql", sql )
      }
    }
    catch
    {
      case ex:Exception =>
      {
        success = false
        message = ex.getMessage
      }
    }
    val info = if (success ) s"generated sql for model: $name" else "error generating sql"
    successOrError(success, sql, Some(info))
  }


  /**
   * gets the database connection for the supplied key and shard.
    *
    * @param dbKey   : the dbKey pointing to the database to install the model to. leave empty to use default db
   * @param dbShard : the dbShard pointing to the database to install the model to. leave empty to use default db
   * @return
   */
  def connection(dbKey:String = "", dbShard:String = ""): Result[DbConString] =
  {
    val con = _entities.getDbConnection(dbKey, dbShard)
      .fold[Result[DbConString]](NoResult)( con => success(con))
    con
  }


  /**
   * generates all the sql install files for all the registered entities
    *
    * @return
   */
  def eachEntity(callback:(EntityInfo) => Result[Any]): Result[String] =
  {
    val all = _entities.getEntities()
    var results = ""
    var success = true
    for( item <- all )
    {
      val result = callback( item )
      if(!result.success)
      {
        success = false
        results += result.msg
      }
    }
    successOrError(success, results)
  }


  /**
   * generates all the sql install files for all the registered entities
    *
    * @return
   */
  def eachEntity(callback:(EntityInfo) => Unit): Unit =
  {
    val all = _entities.getEntities()
    for( item <- all )
    {
      callback( item )
    }
  }
}
