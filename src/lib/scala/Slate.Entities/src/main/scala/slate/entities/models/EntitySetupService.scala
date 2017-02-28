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

package slate.entities.models


import slate.common.databases.{DbCon, DbLookup, DbConString, DbBuilder}
import slate.common.info.Folders
import slate.common.results.ResultSupportIn
import slate.common.{NoResult, Files, Result}
import slate.entities.core.{EntityInfo, Entities}

/**
  * Created by kreddy on 3/23/2016.
  */
class EntitySetupService(val _entities:Entities,
                         val _dbs:Option[DbLookup],
                         val _settings:ModelSettings,
                         val _folders:Option[Folders]) extends ResultSupportIn {

  def names(): List[String] = _entities.getEntities().map( _.entityTypeName)


  /**
   * installs all the registered entities in the database
    *
    * @return
   */
  def installAll(): Result[String] =
  {
    eachEntity( (entity) =>
    {
      install(entity.entityTypeName, "1", entity.dbKey, entity.dbShard )
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
      generateSql(entity.entityTypeName, "1")
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
    val err = s"Unable to install, can not generate sql for model ${name}"
    result.fold[Result[Boolean]](failure(msg = Some(err)))( sql => {
      val db = _entities.getDatabase(dbKey, dbShard)
      db.update(sql)
      ok()
    })
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
    val result = try
    {
      val fullName = name
      val svc = _entities.getServiceByName( fullName )
      val model = svc.repo().mapper().model()
      val sql = new DbBuilder().addTable(model)

      if ( _settings.enableOutput && _folders.isDefined)
      {
        _folders.fold()( folders => {
          Files.writeDatedFile(folders.pathToOutputs, s"model-${model.name}.sql", sql )
        })
      }
      (true, "", sql)
    }
    catch
    {
      case ex:Exception =>
      {
        (false, ex.getMessage, "")
      }
    }
    val success = result._1
    val error = result._2
    val sql = result._3

    val info = if (success ) s"generated sql for model: $name" else "error generating sql"
    successOrError(success, Option(sql), Some(info))
  }


  def connectionByDefault(): Result[DbCon] =
  {
    val con = _dbs.fold[Result[DbCon]](NoResult)( dbs => {
      dbs.default.fold[Result[DbCon]](NoResult)( con => success( con ))
    })
    con
  }


  def connectionByName(name:String): Result[DbCon] =
  {
    val con = _dbs.fold[Result[DbCon]](NoResult)( dbs => {
      dbs.named(name).fold[Result[DbCon]](NoResult)( con => success( con ))
    })
    con
  }


  /**
   * gets the database connection for the supplied key and shard.
    *
    * @param dbKey   : the dbKey pointing to the database to install the model to. leave empty to use default db
   * @param dbShard : the dbShard pointing to the database to install the model to. leave empty to use default db
   * @return
   */
  def connectionByGroup(dbKey:String = "", dbShard:String = ""): Result[DbCon] =
  {
    val con = _dbs.fold[Result[DbCon]](NoResult)( dbs =>
    {
      dbs.group(dbKey, dbShard).fold[Result[DbCon]](NoResult)( con => success(con))
    })
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
    val result = all.foldLeft((true, ""))( (acc, entity) => {
      val result = callback( entity )
      if(!result.success){
        (false, acc._2 + result.msg)
      }
      else
        acc
    })
    val success = result._1
    val message = result._2
    successOrError(success, Option(message))
  }


  /**
   * generates all the sql install files for all the registered entities
    *
    * @return
   */
  def eachEntity(callback:(EntityInfo) => Unit): Unit = {
    _entities.getEntities().foreach(callback)
  }
}
