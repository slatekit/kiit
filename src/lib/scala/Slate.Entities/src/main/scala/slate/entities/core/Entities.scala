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

import scala.reflect.runtime.universe.{typeOf, Type}

import slate.common.databases.{Db, DbConString, DbConstants, DbLookup}
import slate.common.mapper.Mapper
import slate.common.{ListMap, Reflector, Strings}
import slate.common.databases.DbConstants._
import slate.entities.repos.{EntityRepoSqlServer, EntityRepoMySql, EntityRepoInMemory}


import scala.collection.mutable.{Map}


/**
  *  A registry for all the entities and their corresponding services, repositories, database
  *  types, and connection keys.
  *
  *   // Case 1: In-memory
  *   Entities.Register[Invitation](sqlRepo: false);
  *
  *   // Case 2: In-memory, with custom service
  *   Entities.register[Invitation](sqlRepo: false, serviceType: typeof(InvitationService));
  *
  *   // Case 3: Sql-repo
  *   Entities.register[Invitation](sqlRepo: true);
  *
  *   // Case 4: Sql-repo, with custom service
  *   Entities.register[Invitation](sqlRepo: true, serviceType: typeof(InvitationService));
  *
  *   // Case 5: Custom repository
  *   Entities.register[Invitation](sqlRepo: true, repo: new InvitationRepository());
  *
  *   // Case 6: Custom repo with provider type specified
  *   Entities.register[Invitation](sqlRepo: true, repo: new InvitationRepository(),
  *     dbType: "mysql");
  *
  *   // Case 7: Full customization
  *   Entities.register[Invitation](sqlRepo: true, serviceType: typeof(InvitationService),
  *     repo: new InvitationRepository(), dbType: "mysql");
  *
  *   // Case 8: Full customization
  *   Entities.register[Invitation](sqlRepo: true, serviceType: typeof(InvitationService),
  *     repo: new InvitationRepository(), mapper: null, dbType: "mysql");
  *
  */
class Entities(private val _dbs:Option[DbLookup] = None) {

  private val _info = new ListMap[String, EntityInfo]()
  private val _mappers = Map[String, EntityMapper]()


  def register[T >: Null <: IEntity](
                                      isSqlRepo    : Boolean                           ,
                                      entityType   : Type                              ,
                                      serviceType  : Option[Type         ]       = None,
                                      repoType     : Option[Type         ]       = None,
                                      mapperType   : Option[Type         ]       = None,
                                      repository   : Option[EntityRepo[T]]       = None,
                                      mapper       : Option[EntityMapper ]       = None,
                                      dbType       : Option[String       ]       = Some(DbMySql),
                                      dbKey        : Option[String       ]       = None,
                                      dbShard      : Option[String       ]       = None
                                    ): EntityInfo =
  {
    // Create mapper
    val mapr = buildMapper(isSqlRepo, entityType, mapper)

    // Create repo
    val repo = buildRepo[T](isSqlRepo, dbType.getOrElse(""), dbKey.getOrElse(""), dbShard.getOrElse(""), entityType, mapr)

    // Create the service
    val service = buildService[T](serviceType, repo)

    // Now register
    val info = new EntityInfo (
                                  Some(entityType)       ,
                                  serviceType            ,
                                  repoType               ,
                                  mapperType             ,
                                  Option(service)        ,
                                  Option(repo   )        ,
                                  Option(mapr   )        ,
                                  isSqlRepo              ,
                                  dbType .getOrElse("")  ,
                                  dbKey  .getOrElse("")  ,
                                  dbShard.getOrElse("")
    )
    val key = getKey(entityType, dbKey.getOrElse(""), dbShard.getOrElse(""))
    _info(key) = info
    info
  }


  def getRepo(entityType:Type, dbKey:String = "", dbShard:String = ""):IEntityRepo =
  {
    val info = getInfo(entityType, dbKey, dbShard)
    if(!info.entityRepoInstance.isDefined || info.entityRepoInstance.get == null)
      null
    else
      info.entityRepoInstance.get.asInstanceOf[IEntityRepo]
  }


  def getService(entityType:Type, dbKey:String = "", dbShard:String = ""):IEntityService =
  {
    val info = getInfo(entityType, dbKey, dbShard)
    if(!info.entityServiceInstance.isDefined || info.entityServiceInstance.get == null)
      null
    else
      info.entityServiceInstance.get.asInstanceOf[IEntityService]
  }


  def getServiceByName(entityType:String, dbKey:String = "", dbShard:String = ""):IEntityService =
  {
    val info = getInfoByName(entityType, dbKey, dbShard)
    if(!info.entityServiceInstance.isDefined || info.entityServiceInstance.get == null)
      null
    else
      info.entityServiceInstance.get.asInstanceOf[IEntityService]
  }


  def getMapper(entityType:Type):EntityMapper =
  {
    val entityKey = entityType.typeSymbol.fullName
    if(!_mappers.contains(entityKey))
      throw new IllegalArgumentException("mapper not found for :" + entityKey)

    val mapper = _mappers(entityKey)
    mapper
  }


  def getDatabase(dbKey:String = "", dbShard:String = ""): Db =
  {
    val con = getDbConnection()
    require(con.isDefined, s"Database connection for ${dbKey} & ${dbShard} has not been set")
    new Db( con.get ).open()
  }


  def getDb(dbKey:String = "", dbShard:String = ""): Db =
  {
    getDatabase(dbKey, dbShard)
  }


  def getDbConnection(dbKey:String = "", dbShard:String = ""): Option[DbConString] =
  {
    _dbs.fold[Option[DbConString]](None)( dbs => {

      // Case 1: default connection
      if(Strings.isNullOrEmpty(dbKey))
        dbs.default

      // Case 2: named connection
      else if(Strings.isNullOrEmpty(dbShard))
        dbs.named(dbKey)

      // Case 3: shard
      else
        dbs.group(dbKey, dbShard)
    })
  }


  def getInfo(entityType:Type, dbKey:String = "", dbShard:String = ""): EntityInfo =
  {
    val key = getKey(entityType, dbKey, dbShard)
    if(!_info.contains(key))
      throw new IllegalArgumentException(s"invalid entity : $key")
    _info(key)
  }


  def getEntities(): List[EntityInfo] = _info.all.map( i => i )


  def getInfoByName(entityType:String, dbKey:String = "", dbShard:String = ""): EntityInfo =
  {
    val key = buildKey(entityType, dbKey, dbShard)
    if(!_info.contains(key))
      throw new IllegalArgumentException(s"invalid entity : $key")
    _info(key)
  }


  private def getKey(entityType:Type, dbKey:String = "", dbShard:String = ""):String =
  {
    buildKey(entityType.typeSymbol.fullName, dbKey, dbShard)
  }


  private def buildKey(entityType:String, dbKey:String = "", dbShard:String = ""):String =
  {
    entityType + ":" + dbKey + ":" + dbShard
  }


  private def buildMapper(isSqlRepo:Boolean, entityType: Type, mapper:Option[EntityMapper]):EntityMapper = {

    val entityKey = entityType.typeSymbol.fullName

    def createMapper(entityType:Type):EntityMapper = {
      //val entity = Reflector.createInstance(entityType)
      val model = Mapper.loadSchema(entityType)
      val em  = new EntityMapper(model)
      em
    }
    val entityMapper = mapper.getOrElse(createMapper(entityType))
    _mappers(entityKey) = entityMapper
    entityMapper
  }


  private def buildRepo[T >: Null <: IEntity](isSqlRepo:Boolean, dbType:String, dbKey:String, dbShard:String,
                           entityType:Type, mapper:EntityMapper): EntityRepo[T] = {
    // Currently only long supported
    val entityIdType = typeOf[Long]
    val repoType = if(!isSqlRepo) DbMemory else dbType
    val repo = repoType match {

      case DbMemory => {
        new EntityRepoInMemory[T](entityType, Some(entityIdType), Some(mapper))
      }
      case DbMySql => {
        new EntityRepoMySql[T](entityType, Some(entityIdType), Some(mapper), None, getDb(dbKey, dbShard))
      }
      case DbSqlServer => {
        new EntityRepoSqlServer[T](entityType, Some(entityIdType), Some(mapper), None, getDb(dbKey, dbShard))
      }
      case _ => {
        new EntityRepoInMemory[T](entityType, Some(entityIdType), Some(mapper))
      }
    }
    repo
  }


  private def buildService[T >: Null <: IEntity](serviceType:Option[Type], repo:EntityRepo[T]): EntityService[T] = {
    val service = serviceType.fold(new EntityService[T](repo))( s => {
      Reflector.createInstance(s, Some(Seq(repo))).asInstanceOf[EntityService[T]]
    })
    service
  }
}
