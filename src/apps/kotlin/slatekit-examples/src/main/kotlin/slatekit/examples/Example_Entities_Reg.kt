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

package slatekit.examples


//<doc:import_required>
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityInfo
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.Result
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbLookup
import slatekit.common.db.DbTypeMySql
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd
import slatekit.examples.common.User
import slatekit.examples.common.UserRepository
import slatekit.examples.common.UserService
//</doc:import_examples>


/**
 * Created by kreddy on 3/15/2016.
 */
class Example_Entities_Reg : Cmd("types") {

    override fun executeInternal(args: Array<String>?): Result<Any> {
        //<doc:setup>
        // The entities are dependent on the database connections setup.
        // See Example_Database.scala for more info

        // 1. Register the default connection
        val dbs = DbLookup.defaultDb(ConfFuncs.readDbCon("user://.slate/db_default.txt")!!)

        // 2. Register a named connection
        //val dbs = DbLookup.namedDbs(("user_db", DbUtils.loadFromUserFolder(".slate", "db_default.txt"))

        // 3: Register connection as a shard and link to a group
        //val dbs = DbLookup.groupedDbs(("group1", List[(String,DbConString)](("shard1", DbUtils.loadFromUserFolder(".slate", "db_default.txt")))))
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
        val entities = Entities(dbs)

        // Case 1: In-memory
        showResults("Case 1", entities.register<User>(isSqlRepo = false, entityType = User::class))

        // Case 2: In-memory + with custom service
        showResults("Case 2", entities.register<User>(isSqlRepo = false, entityType = User::class,
                serviceType = UserService::class))

        // Case 3: Sql-repo = EntityRepository[T] - mysql, default service ( EntityService[T] )
        // Note: this uses the default database connection above
        showResults("Case 3", entities.register<User>(isSqlRepo = true, entityType = User::class,
                dbType = DbTypeMySql))

        // Case 4: Sql-repo + with custom service = default sql repo ( EntityRepository[T] - mysql )
        // Note: this uses the default database connection above
        showResults("Case 4", entities.register<User>(isSqlRepo = true, entityType = User::class,
                serviceType = UserService::class, dbType = DbTypeMySql))

        // Case 5: Custom repository
        // Note: this uses the default database connection above
        showResults("Case 5", entities.register<User>(isSqlRepo = true, entityType = User::class,
                repository = UserRepository(User::class), dbType = DbTypeMySql))

        // Case 6: Custom service type, custom repository, database type
        showResults("Case 6", entities.register<User>(isSqlRepo = true, entityType = User::class,
                serviceType = UserService::class, repository = UserRepository (User::class), dbType = DbTypeMySql))

        // Case 7: Custom service type, custom repository, database specified
        // Note: this uses the named database connection above called "user_db"
        showResults("Case 7", entities.register<User>(isSqlRepo = true, entityType = User::class,
                serviceType = UserService::class, repository = UserRepository (User::class),
                dbType = DbTypeMySql, dbKey = "user_db"))

        // Case 8: Custom service type, custom repository, database specified, mapper specified
        // Each registration will simply overwrite an existing registration for the same entity type
        showResults("Case 8", entities.register<User>(isSqlRepo = true, entityType = User::class,
                serviceType = UserService::class, repository = UserRepository (User::class),
                dbType = DbTypeMySql))

        // Case 9: Provide a database db key ( e.g. for multiple database connections )
        showResults("Case 9", entities.register<User>(isSqlRepo = true, entityType = User::class,
                serviceType = UserService::class, repository = UserRepository (User::class),
                dbType = DbTypeMySql, dbKey = "user_db"))

        // Case 9: Provide a database db key ( e.g. for multiple database connections )
        showResults("Case 10", entities.register<User>(isSqlRepo = true, entityType = User::class,
                serviceType = UserService::class, repository = UserRepository (User::class),
                dbType = DbTypeMySql, dbKey = "group1", dbShard = "shard1"))
        //</doc:setup>

        //<doc:examples>
        // Use case 1: Get repository
        val repo = entities.getRepo<User>(User::class)

        // Use case 2: Get the service
        val svc = entities.getSvc<User>(User::class)

        // Use case 3: Get the entity mapper
        val mapper = entities.getMapper(User::class)

        // Use case 4: Get the repo for a specific shard
        val repoShard = entities.getRepo<User>(User::class)
        //</doc:examples>

        return ok()
    }


    fun showResults(desc: String, regInfo: EntityInfo): Unit {
        println(desc)
        println(regInfo.toStringDetail())
        println()
    }
}
