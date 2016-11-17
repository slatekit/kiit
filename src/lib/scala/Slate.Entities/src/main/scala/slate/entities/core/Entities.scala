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

import slate.common.databases.{Db, DbConString, DbConstants, DbLookup}
import slate.common.mapper.Mapper
import slate.common.{Ensure, ListMap, Reflector, Strings}
import slate.entities.repos.{EntityRepoSql, EntityRepoMySql, EntityRepoInMemory}

import scala.collection.mutable.{ListBuffer, Map}
import scala.reflect.runtime.universe.Type


/**
  *  Registers all the entities and their corresponding services, repositories, database
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
                                      isSqlRepo    : Boolean             = true,
                                      entityType   : Type                = null,
                                      serviceType  : Type                = null,
                                      repoType     : Type                = null,
                                      mapperType   : Type                = null,
                                      repository   : EntityRepo[T]       = null,
                                      mapper       : EntityMapper        = null,
                                      dbType       : String              = ""  ,
                                      dbKey        : String              = ""  ,
                                      dbShard      : String              = ""
                                    ): EntityInfo =
  {
    // Create instance of repo ( memory or sql backed )
    var repo = repository
    if (repo == null)
    {
      if(!isSqlRepo)
      {
        repo = new EntityRepoInMemory[T](entityType)
      }
      else if(dbType == DbConstants.DbMySql)
      {
        val sqlRepo = new EntityRepoMySql[T](entityType)
        sqlRepo.setDb(getDatabase(dbKey, dbShard))
        repo = sqlRepo
      }
      else if(dbType == DbConstants.DbSqlServer)
      {
        val sqlRepo = new EntityRepoMySql[T](entityType)
        sqlRepo.setDb(getDatabase(dbKey, dbShard))
        repo = sqlRepo
      }
    }
    else {
      if(isSqlRepo){
        repo.asInstanceOf[EntityRepoSql[T]].setDb(getDatabase(dbKey, dbShard))
      }
    }

    // Create mapper
    var mapperFinal = mapper
    val entityKey = entityType.typeSymbol.fullName
    if(mapper == null && !_mappers.contains(entityKey))
    {
      // load attributes
      val entity = Reflector.createInstance(entityType).asInstanceOf[T]
      val model = Mapper.loadSchema(entity, entityType)

      mapperFinal = new EntityMapper(model)
      _mappers(entityKey) = mapperFinal
    }
    else if( mapper != null && isSqlRepo && !_mappers.contains(entityKey))
    {
      _mappers(entityKey) = mapperFinal
    }

    // Create the service
    var service:EntityService[T] = null
    if (serviceType == null)
    {
      service = new EntityService[T]()
    }
    else
    {
      service = Reflector.createInstance(serviceType).asInstanceOf[EntityService[T]]
    }

    // initialize the mapper
    mapperFinal.init()

    // setup the repo with the mapper
    repo.setMapper(mapperFinal)

    // setup the service with the repo
    service.init(repo)

    // Now register
    val info = new EntityInfo (
                                  Some(entityType       ),
                                  Some(serviceType      ),
                                  Some(repoType         ),
                                  Some(mapperType       ),
                                  Some(service          ),
                                  Some(repo             ),
                                  Some(mapperFinal      ),
                                  isSqlRepo              ,
                                  dbType                 ,
                                  dbKey                  ,
                                  dbShard
                              )
    val key = getKey(entityType, dbKey, dbShard)
    _info(key) = info
    info
  }


  def getRepo(entityType:Type, dbKey:String = "", dbShard:String = ""):IEntityRepo =
  {
    val info = getInfo(entityType, dbKey, dbShard)
    if(!info.entityRepoInstance.isDefined || info.entityRepoInstance.get == null)
      return null
    info.entityRepoInstance.get.asInstanceOf[IEntityRepo]
  }


  def getService(entityType:Type, dbKey:String = "", dbShard:String = ""):IEntityService =
  {
    val info = getInfo(entityType, dbKey, dbShard)
    if(!info.entityServiceInstance.isDefined || info.entityServiceInstance.get == null)
      return null
    info.entityServiceInstance.get.asInstanceOf[IEntityService]
  }


  def getServiceByName(entityType:String, dbKey:String = "", dbShard:String = ""):IEntityService =
  {
    val info = getInfoByName(entityType, dbKey, dbShard)
    if(!info.entityServiceInstance.isDefined || info.entityServiceInstance.get == null)
      return null
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
    Ensure.isTrue(con.isDefined, s"Database connection for ${dbKey} & ${dbShard} has not been set")
    new Db( con.get ).open()
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


  def getEntities(): List[EntityInfo] =
  {
    val copy = ListBuffer[EntityInfo]()
    for( item <- _info.all())
    {
      copy += item
    }
    copy.toList
  }


  def getInfoByName(entityType:String, dbKey:String = "", dbShard:String = ""): EntityInfo =
  {
    val key = getKeyByName(entityType, dbKey, dbShard)
    if(!_info.contains(key))
      throw new IllegalArgumentException(s"invalid entity : $key")
    _info(key)
  }


  private def getKey(entityType:Type, dbKey:String = "", dbShard:String = ""):String =
  {
    val key = entityType.typeSymbol.fullName + ":" + dbKey + ":" + dbShard
    key
  }


  private def getKeyByName(entityType:String, dbKey:String = "", dbShard:String = ""):String =
  {
    val key = entityType + ":" + dbKey + ":" + dbShard
    key
  }
}
