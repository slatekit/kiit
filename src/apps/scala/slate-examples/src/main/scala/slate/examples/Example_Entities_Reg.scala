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

package slate.examples

//<doc:import_required>
import slate.common.databases.{DbConfig, DbLookup, DbConstants}
import slate.common.results.ResultSupportIn
import slate.entities.core._
import slate.examples.common._
import slate.examples.common.User
import scala.reflect.runtime.universe._
//</doc:import_required>

//<doc:import_examples>
import slate.common.{Result}
import slate.core.cmds.Cmd
//</doc:import_examples>


/**
  * Created by kreddy on 3/15/2016.
  */
class Example_Entities_Reg extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:setup>
    // The entities are dependent on the database connections setup.
    // See Example_Database.scala for more info

    // 1. Register the default connection
    DbLookup.setDefault( Some(DbConfig.loadFromUserFolder(".slate", "db_default.txt") ) )

    // 2. Register a named connection
    DbLookup.set("user_db", DbConfig.loadFromUserFolder(".slate", "db_default.txt") )

    // 3: Register connection as a shard and link to a group
    DbLookup.shard("group1", "shard1", DbConfig.loadFromUserFolder(".slate", "db_default.txt") )
    //</doc:setup>

    //<doc:examples>
    // The entities can be registered and set up in multiple ways.
    // They can be registered to :
    //
    // - use in-memory repository or a sql ( mysql ) repository
    // - use the default EntityService[T] or a custom EntityService
    // - use a singleton instance or new instance
    // - use a certain type of database ( mysql only for now )
    // - use the default EntityRepository ( mysql ) or a custom repository
    // - use a supplied EntityMapper or a custom mapper
    val entities = new Entities()

    // Case 1: In-memory
    showResults( "Case 1", entities.register[User](isSqlRepo= false, entityType = typeOf[User]))

    // Case 2: In-memory + with custom service
    showResults( "Case 2", entities.register[User](isSqlRepo= false, entityType = typeOf[User],
      serviceType= typeOf[UserService]))

    // Case 3: Sql-repo = EntityRepository[T] - mysql, default service ( EntityService[T] )
    // Note: this uses the default database connection above
    showResults( "Case 3", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      dbType = DbConstants.DbMySql))

    // Case 4: Sql-repo + with custom service = default sql repo ( EntityRepository[T] - mysql )
    // Note: this uses the default database connection above
    showResults( "Case 4", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= typeOf[UserService], dbType = DbConstants.DbMySql))

    // Case 5: Custom repository
    // Note: this uses the default database connection above
    showResults( "Case 5", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      repository= new UserRepository(typeOf[User]), dbType = DbConstants.DbMySql))

    // Case 6: Custom service type, custom repository, database type
    showResults( "Case 6", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= typeOf[UserService], repository= new UserRepository(typeOf[User]), dbType= "mysql"))

    // Case 7: Custom service type, custom repository, database specified
    // Note: this uses the named database connection above called "user_db"
    showResults( "Case 7", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= typeOf[UserService], repository= new UserRepository(typeOf[User]),
      dbType= "mysql", dbKey = "user_db"))

    // Case 8: Custom service type, custom repository, database specified, mapper specified
    // Each registration will simply overwrite an existing registration for the same entity type
    showResults( "Case 8", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= typeOf[UserService], repository= new UserRepository(typeOf[User]),
      mapper= null, dbType= "mysql"))

    // Case 9: Provide a database db key ( e.g. for multiple database connections )
    showResults( "Case 9", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= typeOf[UserService], repository= new UserRepository(typeOf[User]),
      mapper= null, dbType= "mysql", dbKey = "user_db"))

    // Case 9: Provide a database db key ( e.g. for multiple database connections )
    showResults( "Case 10", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= typeOf[UserService], repository= new UserRepository(typeOf[User]),
      mapper= null, dbType= "mysql", dbKey = "group1", dbShard = "shard1"))
    //</doc:setup>

    //<doc:examples>
    // Use case 1: Get repository
    val repo = entities.getRepo(typeOf[User])

    // Use case 2: Get the service
    val svc = entities.getService(typeOf[User])

    // Use case 3: Get the entity mapper
    val mapper = entities.getMapper(typeOf[User])

    // Use case 4: Get the repo for a specific shard
    val repoShard = entities.getRepo(typeOf[User])
    //</doc:examples>

    ok()
  }


  def showResults(desc:String, regInfo:EntityInfo):Unit =
  {
    println(desc)
    println(regInfo.toStringDetail())
    println()
  }
}
