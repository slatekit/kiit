package slatekit.examples

import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.*
import slatekit.common.conf.Config
import slatekit.common.data.*
import slatekit.db.Db
import slatekit.entities.*
import slatekit.common.data.Mapper
import slatekit.common.utils.ListMap
import slatekit.common.utils.RecordMap
import slatekit.entities.repos.InMemoryRepo
import slatekit.meta.Schema
import slatekit.meta.models.FieldCategory
import slatekit.meta.models.ModelMapper
import slatekit.orm.OrmMapper
import slatekit.query.Op
import slatekit.query.Query
import slatekit.results.Success
import slatekit.results.Try
import java.util.*


/**
 * Created by kreddy on 3/15/2016.
 */

class Guide_ORM : Command("types") {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:setup>
        model()
        // The entities are dependent on the database connections setup.
        // See Example_Database.kt for more info


        return Success("")
    }

    // Sample model
    data class City(override val id:Long, val name:String, val alias:String) : EntityWithId<Long> {
        override fun isPersisted(): Boolean {
            return id > 0
        }
    }


    fun con() {

        // Connection
        // 1. Explicit creation
        val con1 = DbConString(Vendor.MySql.driver, "jdbc:mysql://localhost/default", "user1", "pswd3210")

        // 2. From config
        val cfg = Config.of("env.dev.conf")
        val con2 = cfg.dbCon("db")

        // Connections ( collection of multiple connections )
        // 1. From single connection
        val cons1 = Connections.of(con1)

        // 2. From config: Shortcut for Connections.of(conf.dbCon("db"))
        val cons2 = Connections.from(cfg)

        // 3. From multiple named connections
        val cons3 = Connections.named(
                listOf(
                        Pair("db1", con1),
                        Pair("db2", con2)
                ))
    }


    fun db() {
        val con1 = DbConString(Vendor.MySql.driver, "jdbc:mysql://localhost/default", "user1", "pswd3210")
        val db = Db(con1)

        // Inserts
        val id1 = db.insert("insert into `city`(`name`) values( 'ny' )")
        val id2 = db.insert("insert into `city`(`name`) values( ? )", listOf("ny"))
        val id3 = db.insertGetId("insert into `city`(`name`) values( ? )", listOf("ny")).toLong()

        // Updates
        val updated1 = db.update("update `city` set `alias` = 'nyc' where id = 2")
        val updated2 = db.update("update `city` set `alias` = 'nyc' where id = ?", listOf(id2))

        // Deletes
        val deleted1 = db.update("delete from `city` where id = 2")
        val deleted2 = db.update("delete from `city` where id = ?", listOf(2))

        // Procs
        val procUpdate1 = db.callUpdate("dbtests_activate_by_id", listOf(2))
        val procQuery1 = db.callQuery("dbtests_max_by_id",
                callback = { rs -> rs.getString(0) }, inputs = listOf(id2))

        // Queries ( mapOne, mapMany )
        val city1 = db.mapOne<City>("select * from `city` where id = ?", listOf(1)) { rs ->
            City(rs.getLong("id"), rs.getString("name"), rs.getString("alias"))
        }
        val city2 = db.mapAll<City>("select * from `city` where id < ?", listOf(2)) { rs ->
            City(rs.getLong("id"), rs.getString("name"), rs.getString("alias"))
        }

        // Scalar calls
        val total1 = db.getScalarBool("select isActive from users where userid = ?", listOf(1))
        val total2 = db.getScalarInt("select age from users where userid = ?", listOf(1))
        val total3 = db.getScalarLong("select account from users where userid = ?", listOf(1))
        val total4 = db.getScalarFloat("select salary from users where userid = ?", listOf(1))
        val total5 = db.getScalarDouble("select total from users where userid = ?", listOf(1))
        val total6 = db.getScalarString("select email from users where userid = ?", listOf(1))
        val total7 = db.getScalarLocalDate("select startDate from users where userid = ?", listOf(1))
        val total8 = db.getScalarLocalTime("select startHour from users where userid = ?", listOf(1))
        val total9 = db.getScalarLocalDateTime("select registered from users where userid = ?", listOf(1))
        val total10 = db.getScalarZonedDateTime("select activated from users where userid = ?", listOf(1))
    }


    fun records(){

        // Sample connection/DB
        val con1 = DbConString(Vendor.MySql.driver, "jdbc:mysql://localhost/default", "user1", "pswd3210")
        val db = Db(con1)

        // Record via wrapped JDBC ResultSet
        val city = db.mapOne<City>("select * from `city` where id = ?", listOf(1)) { rs:Record ->
            City(   rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("alias")
            )
        }

        // Simulating a Record from a list of key/value pairs
        val record:Record = RecordMap(
                ListMap(
                        listOf(
                                Pair("id", 1L),
                                Pair("uuid", "ABC"),
                                Pair("email", "kishore@abc.com"),
                                Pair("isActive", true),
                                Pair("age", 35),
                                Pair("status", Status.InActive),
                                Pair("salary", 400.5),
                                Pair("uid", UUID.fromString("ad6ec896-bc1e-4430-b13c-88e3d4924a6a")),
                                Pair("createdAt", DateTimes.of(2017, 1, 1, 12, 0, 0, 0))
                                )
                )
        )

        // There are getX methods, getXOrNull, getXOrDefault
        println(record.getBool("isActive"))
        println(record.getBoolOrNull("isActive"))
        println(record.getBoolOrElse("isActive", false))

        // There are several methods for various types
        println(record.getString("email"))
        println(record.getBool("isActive"))
        println(record.getInt("age"))
        println(record.getLong("id"))
        println(record.getDouble("salary"))
        println(record.getUUID("uuid"))
        println(record.getZonedDateTime("createdAt"))
    }


    fun h2(){

        // 1. Connection
        val conh2 = DbConString(Vendor.H2.driver, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")

        // 2. Database ( JDBC abstraction )
        val db = Db(conh2).open()

        // 3. Database usage
        db.execute("CREATE TABLE PERSON(id int primary key, name varchar(255))")
        db.insert("INSERT INTO PERSON" + "(id, name) values" + "(?,?)", listOf(1,"batman@gotham.com"))
        val users = db.mapAll( "select * from PERSON", null) {
            User(it.getInt("id").toLong(),it.getString("name"))
        }
    }


    fun h2_repo(){

        // 1. Connection
        val con = DbConString(Vendor.H2.driver, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")

        // 2. Database ( thin JDBC abstraction to support Server + Android )
        val db = Db.open(con)

        // 3. Mapper: manual field mapping
        val mapper = object: Mapper<Long, User> {

            override fun encode(model:User, action: DataAction): Values {
                return listOf(
                        Value("id", model.id),
                        Value("firstname", model.first),
                        Value("lastname", model.last),
                        Value("email", model.email)
                )
            }

            override fun decode(record: Record): User? {
                return User(
                        id = record.getInt("id").toLong(),
                        first = record.getString("first"),
                        last = record.getString("last"),
                        email = record.getString("email")
                )
            }
        }

        // 4. Repo : CRUD repository
        val repo1 = Repo.h2<Long, User>(db, mapper)


        db.execute("CREATE TABLE PERSON(id int primary key, name varchar(255))")

    }


    fun mapper():Mapper<Long, City> {
        val mapper = object: Mapper<Long, City> {

            override fun encode(model:City, action: DataAction): Values {
                return listOf(
                        Value("id", model.id),
                        Value("name", model.name),
                        Value("alias", model.alias)
                )
            }

            override fun decode(record: Record): City? {
                return City(
                        id = record.getInt("id").toLong(),
                        name = record.getString("name"),
                        alias = record.getString("alias")
                )
            }
        }
        return mapper
    }


    fun repo() {
        // 1. Connection
        val con = DbConString(Vendor.H2.driver, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")

        // 2. Database ( thin JDBC abstraction to support Server + Android )
        val db = Db.open(con)
        val repo = InMemoryRepo.of<Long, City>()

        // CRUD
        val city = City(0, "Brooklyn", "bk")

        // Create
        val id = repo.create(city)
        repo.save(City(0, "New York", "NYC"))

        // Checks
        repo.any()
        repo.count()

        // Gets
        repo.getAll()
        repo.getById(1)
        repo.getByIds(listOf(1, 2))
        repo.first()
        repo.last()
        repo.recent(10)
        repo.oldest(10)

        // Finds
        val item1 = repo.findOneByField("name", Op.Eq, "Brooklyn")
        val items2 = repo.findByField("name", Op.Eq, "Brooklyn")
        val items3 = repo.findByFields(listOf(Pair("name", "Brooklyn")))
        val items4= repo.findIn("name", listOf("Queens", "Brooklyn"))
        repo.find(repo.query())

        // Updates
        val updated = city.copy(id = id, name = "Queens")
        repo.update(updated)
        repo.patchById(id, listOf(Pair("name", "Queens City"), Pair("alias", "QCity")))
        repo.patchByFields(listOf("name" to "Queens"), listOf("name" to "Queens City"))
        repo.patchByField("tag", "test")
        repo.updateByProc("update_alias", listOf(1, "QCity"))

        // Deletes
        repo.deleteAll()
        repo.delete(city)
        repo.deleteById(2)
        repo.deleteByIds(listOf(1, 2))
        repo.deleteByField(City::id.name, Op.Eq, 1)
        repo.deleteByQuery(repo.query().where(City::id.name, Op.Eq, 1))
    }


    fun service(){
        // Setup: This is boiler-plate that can be moved
        // to a helper function/builder
        // 1. connection
        // 2. database
        // 3. mapper
        // 4. repo
        // 5. service
        val con = DbConString(Vendor.H2.driver, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
        val db = Db.open(con)
        val mapper:Mapper<Long, City> = mapper()
        val repo = Repo.h2<Long, City>(db, mapper)
        val service = EntityService<Long, City>(repo)

        // CRUD operations
        val city = City(0, "Brooklyn", "bk")

        // Create
        val id = service.create(city)
        service.save(City(0, "New York", "NYC"))

        // Checks
        service.any()
        service.count()

        // Gets
        service.getAll()
        service.getById(1)
        service.getByIds(listOf(1, 2))
        service.first()
        service.last()
        service.recent(10)
        service.oldest(10)

        // Finds
        val item1 = service.findOneByField(User::email, Op.Eq,"Brooklyn")
        val items2 = service.findByField(City::name, Op.Eq, "Brooklyn")
        val items3 = service.findByFields(listOf(Pair("name", "Brooklyn")))
        val items4= service.findIn(City::name, listOf("Queens", "Brooklyn"))
        val items5 = service.findByQuery(Query().where(City::name.name, Op.Eq, "Brooklyn"))

        // Updates
        val updated = city.copy(id = id, name = "Queens")
        service.update(updated)
        service.patch(id, listOf(Pair("name", "Queens City"), Pair("alias", "QCity")))
        service.patchByFields(City::name, "Queens", "Queens City")
        service.patchByField(City::name, "test")
        service.updateByProc("update_alias", listOf(1, "QCity"))

        // Deletes
        service.deleteAll()
        service.delete(city)
        service.deleteById(2)
        service.deleteByIds(listOf(1, 2))
        service.deleteByField(City::id, Op.Eq, 1)
        service.deleteByQuery(Query().where(City::id.name, Op.Eq, 1))

    }

    data class User(
            val id:Long = 0L,
            val email:String,
            val first:String = "",
            val last:String = "",
            val active:Boolean = false,
            val age:Int = 35,
            val salary:Double = 100.00,
            val registered:DateTime? = null,
            val createdAt:DateTime = DateTime.now(),
            val updatedAt:DateTime = DateTime.now()
    )


    object UserSchema : Schema<Long, User>(Long::class, User::class, "user") {
        val id         = id    (User::id        )
        val email      = field  (User::email     , min = 10, max = 50, indexed = true)
        val first      = field  (User::first     , min = 10, max = 50)
        val last       = field  (User::last      , min = 10, max = 50)
        val age        = field  (User::age       )
        val salary     = field  (User::salary    )
        val active     = field  (User::active    )
        val registered = field  (User::registered)
        val createdAt  = field  (User::updatedAt , category = FieldCategory.Meta)
        val updatedAt  = field  (User::updatedAt , category = FieldCategory.Meta)
    }

    fun model(){
        val model = UserSchema.model
        model.fields.forEach {
            println("field: name=${it.name}, ${it.storedName}, ${it.isRequired}, ${it.dataTpe}")
        }

        val model2 = ModelMapper.loadSchema(User::class)
        println("done")
    }

    fun orm(){
        // Setup: This is boiler-plate that can be moved
        // to a helper function/builder
        // 1. connection
        // 2. database
        // 3. mapper
        // 4. repo
        // 5. service
        val con = DbConString(Vendor.H2.driver, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
        val db = Db.open(con)
        val model = UserSchema.model
        val mapper:Mapper<Long, City> = OrmMapper<Long, City>(model, db, Long::class, City::class)
        val repo = Repo.h2<Long, City>(db, mapper)
        val service = EntityService<Long, City>(repo)

        service.save(City(0, "New York", "NYC"))

        // CRUD operations
        service.any()
        service.count()
        service.getById(1)
    }

//        val con2 = default(ConfFuncs.readDbCon("user://.slate/db_default.txt")!!)
//
//        // 2. Create a Connections using an explicit connection string
//        // NOTE: Avoid using explicit connection strings in code.
//        val dbLookup2 = default(
//                DbConString(
//                        "com.mysql.jdbc.Driver",
//                        "jdbc:mysql://localhost/default",
//                        "root",
//                        "abcdefghi"
//                )
//        )
//
//        // 3. Create a Connections using multiple named databases.
//        val dbLookup3 = named(listOf(
//                Pair(
//                        "movies", DbConString(
//                        "com.mysql.jdbc.Driver",
//                        "jdbc:mysql://localhost/movies",
//                        "root",
//                        "abcdefghi"
//                )
//                ),
//                Pair(
//                        "files", DbConString(
//                        "com.mysql.jdbc.Driver",
//                        "jdbc:mysql://localhost/files",
//                        "root",
//                        "abcdefghi"
//                )
//                )
//        ))
//
//    }


    /*
    fun service() {

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
        val ent = Entities()
        val service = MovieService(ctx, ent, InMemoryRepo<Movie>(Movie::class))

        // CASE 1: Create 3-4 users for showing use-cases
        service.create(Movie(0L, "Batman Begins"     , "action", false, 50, 4.2, DateTimes.of(2005,1,1)))
        service.create(Movie(0L, "Dark Knight"      , "action", false, 100,4.5, DateTimes.of(2012,1,1)))
        service.create(Movie(0L, "Dark Knight Rises", "action", false, 120,4.2, DateTimes.of(2012,1,1)))

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
        println(service.findByQuery(Query().where("playing", "=", true)))


        // More docs coming soon.
    }


    fun repo_setup():Unit {
        // =================================================================================
        // CASE 1: In-memory ( non-persisted ) repository has limited functionality
        // but is very useful for rapid prototyping of a data model when you are trying to
        // figure out what fields/properties should exist on the model
        val repo = InMemoryRepo<Movie>(Movie::class)


        // CASE 2: My-sql ( persisted ) repository can be easily setup
        // More examples of database setup/entity registration available in Setup/Registration docs.
        // 2.1: First setup the database
        val db = Db(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/user_db", "root", "abcdefghi"))

        // 2.2: Setup the mapper
        // NOTE: This assumes the entity has annotations on the properties.
        // If you do not want to use annotations, looks at the mapper/model
        // examples for alternative approaches.
        val model = ModelMapper.loadSchema(Movie::class)
        val mapper = EntityMapper(model, MySqlConverter)

        // 2.3: Now create the repo with database and mapper
        val repoMySql = MySqlRepo<Movie>(db, Movie::class, null, mapper)
    }


    fun repo_usage():Unit {

        val repo = InMemoryRepo<Movie>(Movie::class)

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
        val mapper = EntityMapper (schema, MySqlConverter)

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
        val sqlCreate = mapper.mapFields(null, movie, schema, false)
        println(sqlCreate)

        // CASE 3: Get the sql for update
        val sqlForUpdate = mapper.mapFields(null, movie, schema, true)
        println(sqlForUpdate)

        // CASE 4: Generate the table schema for mysql from the model
        //println("table sql : " + buildAddTable(MySqlBuilder(), schema))
    }



    fun showResults(desc: String, regInfo: EntityContext) {
        println(desc)
        println(regInfo.toStringDetail())
        println()
    }


    fun printAll(tag: String, models: List<Movie>) {
        println()
        println(tag.toUpperCase())
        for (model in models)
            printOne(null, model)
    }


    fun printOne(tag: String?, model: Movie?) {
        tag?.let { t ->
            println()
            println(t.toUpperCase())
        }

        model?.let { m ->
            println("User: " + m.id + ", " + m.title + ", " + m.category)
        }
    }

    */
}
