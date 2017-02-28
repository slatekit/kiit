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

import scala.reflect.runtime.universe.{typeOf, Type, TypeTag}

import slate.common.databases._
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


  def register[T >: Null <: Entity](
                                      isSqlRepo    : Boolean,
                                      entityType   : Type,
                                      serviceType  : Option[Type]          = None,
                                      repoType     : Option[Type]          = None,
                                      mapperType   : Option[Type]          = None,
                                      repository   : Option[EntityRepo[T]] = None,
                                      mapper       : Option[EntityMapper ] = None,
                                      dbType       : Option[DbType]        = Some(DbTypeMySql),
                                      dbKey        : Option[String]        = None,
                                      dbShard      : Option[String]        = None,
                                      serviceCtx   : Option[Any]           = None
                                    ): EntityInfo =
  {
    val db = dbType.getOrElse(DbTypeMySql)

    // Create mapper
    val mapr = buildMapper(isSqlRepo, entityType, mapper)

    // Create repo
    val repo = buildRepo[T](isSqlRepo, db, dbKey.getOrElse(""), dbShard.getOrElse(""), entityType, mapr)

    // Create the service
    val service = buildService[T](serviceType, repo, serviceCtx)

    // Now register
    val info = new EntityInfo (
                                  entityType                      ,
                                  serviceType                     ,
                                  repoType                        ,
                                  mapperType                      ,
                                  Option(service)                 ,
                                  Option(repo   )                 ,
                                  Option(mapr   )                 ,
                                  isSqlRepo                       ,
                                  db                              ,
                                  dbKey  .getOrElse("")           ,
                                  dbShard.getOrElse("")
    )
    val key = getKey(entityType, dbKey.getOrElse(""), dbShard.getOrElse(""))
    _info(key) = info
    info
  }


  def getRepo[T >: Null <: Entity](dbKey:String = "", dbShard:String = "")
                                  (implicit tag:TypeTag[T]): EntityRepo[T] = {
    getRepoByType(tag.tpe, dbKey, dbShard).asInstanceOf[EntityRepo[T]]
  }


  def getRepoByType(entityType:Type, dbKey:String = "", dbShard:String = ""):IEntityRepo =
  {
    val info = getInfo(entityType, dbKey, dbShard)
    info.entityRepoInstance.get
  }


  def getSvc[T >: Null <: Entity]( dbKey:String = "", dbShard:String = "")
                                  (implicit tag:TypeTag[T]): EntityService[T] = {
    getService(tag.tpe, dbKey, dbShard).asInstanceOf[EntityService[T]]
  }


  def getSvcByType[T >: Null <: Entity]( tpe:Type, dbKey:String = "", dbShard:String = ""): EntityService[T] = {
    getService(tpe, dbKey, dbShard).asInstanceOf[EntityService[T]]
  }


  def getService(entityType:Type, dbKey:String = "", dbShard:String = ""):IEntityService =
  {
    val info = getInfo(entityType, dbKey, dbShard)
    info.entityServiceInstance.get
  }


  def getServiceByName(entityType:String, dbKey:String = "", dbShard:String = ""):IEntityService =
  {
    val info = getInfoByName(entityType, dbKey, dbShard)
    info.entityServiceInstance.get
  }


  def getMapper[T:TypeTag](): EntityMapper = getMapper(typeOf[T])


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


  def getDbConnection(dbKey:String = "", dbShard:String = ""): Option[DbCon] =
  {
    _dbs.fold[Option[DbCon]](None)( dbs => {

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
    require(_info.contains(key), "Entity invalid or not registered with key : " + key)
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


  private def buildRepo[T >: Null <: Entity](isSqlRepo:Boolean, dbType:DbType, dbKey:String, dbShard:String,
                           entityType:Type, mapper:EntityMapper): EntityRepo[T] = {
    // Currently only long supported
    val entityIdType = typeOf[Long]
    val repoType = if(!isSqlRepo) DbTypeMemory else dbType
    val repo = repoType match {

      case DbTypeMemory => {
        new EntityRepoInMemory[T](entityType, Some(entityIdType), Some(mapper))
      }
      case DbTypeMySql => {
        new EntityRepoMySql[T](entityType, Some(entityIdType), Some(mapper), None, getDb(dbKey, dbShard))
      }
      case DbTypeSqlServer => {
        new EntityRepoSqlServer[T](entityType, Some(entityIdType), Some(mapper), None, getDb(dbKey, dbShard))
      }
      case _ => {
        new EntityRepoInMemory[T](entityType, Some(entityIdType), Some(mapper))
      }
    }
    repo
  }


  private def buildService[T >: Null <: Entity](serviceType:Option[Type],
                                                 repo:EntityRepo[T],
                                                 ctx:Option[Any] ): EntityService[T] = {
    val service = serviceType.fold(new EntityService[T](repo))( s => {
      val args = ctx.fold[Seq[Any]]( Seq(repo))( arg => Seq(arg, repo))
      Reflector.createInstance(s, Some(args)).asInstanceOf[EntityService[T]]
    })
    service
  }
}
