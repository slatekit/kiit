package slatekit.examples

import slatekit.common.DateTime
import slatekit.common.Result
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.Db
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.db.DbLookup.DbLookupCompanion.namedDbs
import slatekit.common.db.DbTypeMySql
import slatekit.common.db.types.DbSourceMySql
import slatekit.meta.models.*
import slatekit.common.Mapper
import slatekit.common.query.Query
import slatekit.common.results.ResultFuncs
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityInfo
import slatekit.entities.core.EntityMapper
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.entities.repos.EntityRepoMySql
import slatekit.examples.common.*
import slatekit.meta.buildAddTable


/**
 * Created by kreddy on 3/15/2016.
 */
class Guide_ORM : Cmd("types") {

    override fun executeInternal(args: Array<String>?): Result<Any> {
        //<doc:setup>
        // The entities are dependent on the database connections setup.
        // See Example_Database.scala for more info

        // 1. Create DbLookup using a connection from a file
        // NOTE: This is safer and more secure as it loads
        // the connection string from the user directory in folder ./slate
        val dbLookup1 = defaultDb(ConfFuncs.readDbCon("user://.slate/db_default.txt")!!)

        // 2. Create a DbLookup using an explicit connection string
        // NOTE: Avoid using explicit connection strings in code.
        val dbLookup2 = defaultDb(
                DbConString(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://localhost/default",
                        "root",
                        "abcdefghi"
                )
        )

        // 3. Create a DbLookup using multiple named databases.
        val dbLookup3 = namedDbs(listOf(
                Pair(
                        "movies", DbConString(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://localhost/movies",
                        "root",
                        "abcdefghi"
                )
                ),
                Pair(
                        "files", DbConString(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://localhost/files",
                        "root",
                        "abcdefghi"
                )
                )
        ))

        // =================================================================================
        // The entities can be registered and set up in multiple ways.
        // They can be registered to :
        //
        // - use in-memory repository or a sql ( mysql ) repository
        // - use the default EntityService[T] or a custom EntityService
        // - use a singleton instance or new instance
        // - use a certain type of database ( mysql only for now )
        // - use the default EntityRepository ( mysql ) or a custom repository
        // - use a supplied EntityMapper or a custom mapper
        val entities = Entities(dbLookup1)

        // Case 1: In-memory
        showResults("Case 1", entities.register<Movie>(isSqlRepo = false, entityType = Movie::class))

        // Case 2: In-memory + with custom service
        showResults("Case 2", entities.register<Movie>(isSqlRepo = false, entityType = Movie::class,
                serviceType = MovieService::class))

        // Case 3: Sql-repo = EntityRepository[T] - mysql, default service ( EntityService[T] )
        // Note: this uses the default database connection above
        showResults("Case 3", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
                dbType = DbTypeMySql))

        // Case 4: Sql-repo + with custom service = default sql repo ( EntityRepository[T] - mysql )
        // Note: this uses the default database connection above
        showResults("Case 4", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
                serviceType = MovieService::class, dbType = DbTypeMySql))

        // Case 5: Custom repository
        // Note: this uses the default database connection above
        showResults("Case 5", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
                repository = MovieRepository(), dbType = DbTypeMySql))

        // Case 6: Custom service type, custom repository, database type
        showResults("Case 6", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
                serviceType = MovieService::class, repository = MovieRepository(), dbType = DbTypeMySql))

        // Case 7: Custom service type, custom repository, database specified
        // Note: this uses the named database connection above called "Movie_db"
        showResults("Case 7", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
                serviceType = MovieService::class, repository = MovieRepository(),
                dbType = DbTypeMySql, dbKey = "Movie_db"))

        // Case 8: Custom service type, custom repository, database specified, mapper specified
        // Each registration will simply overwrite an existing registration for the same entity type
        showResults("Case 8", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
                serviceType = MovieService::class, repository = MovieRepository(),
                dbType = DbTypeMySql))

        // Case 9: Provide a database db key ( e.g. for multiple database connections )
        showResults("Case 9", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
                serviceType = MovieService::class, repository = MovieRepository(),
                dbType = DbTypeMySql, dbKey = "Movie_db"))

        // Case 9: Provide a database db key ( e.g. for multiple database connections )
        showResults("Case 10", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
                serviceType = MovieService::class, repository = MovieRepository(),
                dbType = DbTypeMySql, dbKey = "group1", dbShard = "shard1"))

        // Use case 1: Get repository
        val repo = entities.getRepo<Movie>(Movie::class)

        // Use case 2: Get the service
        val svc = entities.getSvc<Movie>(Movie::class)

        // Use case 3: Get the entity mapper
        val mapper = entities.getMapper(Movie::class)

        // Use case 4: Get the repo for a specific shard
        val repoShar = entities.getRepo<Movie>(Movie::class)

        return ok()
    }


    fun service():Unit {

        // =================================================================================
        // The Service layer initialized with an repository .
        // Purpose of the service layer is to:
        //
        // 1. Delegate to underlying repository for CRUD operations after applying any business logic.
        // 2. Provide a layer to insert business logic during other operations.
        // 3. Has some( not all ) methods that match the repository CRUD, Find, Delete methods
        // 4. Subclassed to perform more complex business logic that may still involve using the repo.
        //
        val ctx = AppContext.simple("sampleapp1")
        val service = MovieService(ctx, EntityRepoInMemory<Movie>(Movie::class))

        // CASE 1: Create 3-4 users for showing use-cases
        service.create(Movie(0L, "Batman Begins"     , "action", false, 50, 4.2, DateTime.of(2005,1,1)))
        service.create(Movie(0L, "Dark Knight"      , "action", false, 100,4.5, DateTime.of(2012,1,1)))
        service.create(Movie(0L, "Dark Knight Rises", "action", false, 120,4.2, DateTime.of(2012,1,1)))

        // CASE 2: Get by id
        printOne("2", service.get(2))

        // CASE 3: Update
        val item2 = service.get(2)
        item2?.let { item ->
            val updated = item.copy(title = "Batman: Dark Knight")
            service.update(updated)
        }

        // CASE 4: Get all
        printAll("all", service.getAll())

        // CASE 5: Get recent/last 2
        printAll("recent", service.recent(2))

        // CASE 6: Get oldest 2
        printAll("oldest", service.oldest(2))

        // CASE 7: Get first one ( oldest )
        printOne("first", service.first())

        // CASE 8: Get last one ( recent )
        printOne("last", service.last())

        // CASE 9: Delete by id
        service.deleteById(4)

        // CASE 10: Get total ( 4 )
        println(service.count())

        // CASE 11: Type-Safe queryusing property type reference
        println(service.findByField(Movie::playing, true))

        // CASE 12: Query
        println(service.find(Query().where("playing", "=", true)))


        // More docs coming soon.
    }


    fun repo_setup():Unit {
        // =================================================================================
        // CASE 1: In-memory ( non-persisted ) repository has limited functionality
        // but is very useful for rapid prototyping of a data model when you are trying to
        // figure out what fields/properties should exist on the model
        val repo = EntityRepoInMemory<Movie>(Movie::class)


        // CASE 2: My-sql ( persisted ) repository can be easily setup
        // More examples of database setup/entity registration available in Setup/Registration docs.
        // 2.1: First setup the database
        val db = Db(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/user_db", "root", "abcdefghi"))

        // 2.2: Setup the mapper
        // NOTE: This assumes the entity has annotations on the properties.
        // If you do not want to use annotations, looks at the mapper/model
        // examples for alternative approaches.
        val model = ModelMapper.loadSchema(Movie::class)
        val mapper = EntityMapper(model)

        // 2.3: Now create the repo with database and mapper
        val repoMySql = EntityRepoMySql<Movie>(db, Movie::class, null, mapper)
    }


    fun repo_usage():Unit {

        val repo = EntityRepoInMemory<Movie>(Movie::class)

        // CASE 1: Create 3-4 users for showing use-cases
        repo.create(Movie(0L, "Batman Begins"     , "action", false, 50, 4.2, DateTime.of(2005,1,1)))
        repo.create(Movie(0L, "Dark Knight"      , "action", false, 100,4.5, DateTime.of(2012,1,1)))
        repo.create(Movie(0L, "Dark Knight Rises", "action", false, 120,4.2, DateTime.of(2012,1,1)))

        // CASE 2: Get by id
        printOne("2", repo.get(2))

        // CASE 3: Update
        val item2 = repo.get(2)
        item2?.let { item ->
            val updated = item.copy(title = "Batman: Dark Knight")
            repo.update(updated)
        }

        // CASE 4: Get all
        printAll("all", repo.getAll())

        // CASE 5: Get recent/last 2
        printAll("recent", repo.recent(2))

        // CASE 6: Get oldest 2
        printAll("oldest", repo.oldest(2))

        // CASE 7: Get first one ( oldest )
        printOne("first", repo.first())

        // CASE 8: Get last one ( recent )
        printOne("last", repo.last())

        // CASE 9: Delete by id
        repo.delete(4)

        // CASE 10: Get total ( 4 )
        println(repo.count())

        // CASE 11: Query
        println(repo.find(Query().where("playing", "=", true)))
    }


    fun mapper_setup():Unit {
        // CASE 1: Load the schema from the annotations on the model
        val schema1 = ModelMapper.loadSchema(Movie::class)


        // CASE 2: Load the schema manually using properties for type-safety
        val schema2 = Model(Movie::class)
                .addId(Movie::id, true)
                .add(Movie::title     , "Title of movie"         , 5, 30)
                .add(Movie::category  , "Category (action|drama)", 1, 20)
                .add(Movie::playing   , "Whether its playing now")
                .add(Movie::rating    , "Rating from users"      )
                .add(Movie::released  , "Date of release"        )
                .add(Movie::createdAt , "Who created record"     )
                .add(Movie::createdBy , "When record was created")
                .add(Movie::updatedAt , "Who updated record"     )
                .add(Movie::updatedBy , "When record was updated")


        // CASE 3: Load the schema manually using named fields
        val schema3 = Model(Example_Mapper.Movie::class)
                .addId(Movie::id, true)
                .addText    ("title"     , "Title of movie"         , true, 1, 30)
                .addText    ("category"  , "Category (action|drama)", true, 1, 20)
                .addBool    ("playing"   , "Whether its playing now")
                .addDouble  ("rating"    , "Rating from users"      )
                .addDateTime("released"  , "Date of release"        )
                .addDateTime("createdAt" , "Who created record"     )
                .addLong    ("createdBy" , "When record was created")
                .addDateTime("updatedAt" , "Who updated record"     )
                .addLong    ("updatedBy" , "When record was updated")

    }


    fun mapper_usage():Unit {
        val schema = ModelMapper.loadSchema(Movie::class)

        // CASE 1: Create mapper with the schema
        val mapper = EntityMapper (schema)

        // Create sample instance to demo the mapper
        val movie = Example_Mapper.Movie(
                title = "Man Of Steel",
                category = "action",
                playing = false,
                cost = 100,
                rating = 4.0,
                released = DateTime.of(2015, 7, 4)
        )

        // CASE 2: Get the sql for create
        val sqlCreate = mapper.mapToSql(movie, update = false, fullSql = true)
        println(sqlCreate)

        // CASE 3: Get the sql for update
        val sqlForUpdate = mapper.mapToSql(movie, update = true, fullSql = true)
        println(sqlForUpdate)

        // CASE 4: Generate the table schema for mysql from the model
        println("table sql : " + buildAddTable(DbSourceMySql(), schema))
    }



    fun showResults(desc: String, regInfo: EntityInfo): Unit {
        println(desc)
        println(regInfo.toStringDetail())
        println()
    }


    fun printAll(tag: String, models: List<Movie>): Unit {
        println()
        println(tag.toUpperCase())
        for (model in models)
            printOne(null, model)
    }


    fun printOne(tag: String?, model: Movie?): Unit {
        tag?.let { t ->
            println()
            println(t.toUpperCase())
        }

        model?.let { m ->
            println("User: " + m.id + ", " + m.title + ", " + m.category)
        }
    }
}
