/**
<slate_header>
author: Kishore Reddy
url: www.github.com/code-helix/slatekit
copyright: 2015 Kishore Reddy
license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
desc: A tool-kit, utility library and server-backend
usage: Please refer to license on github for more info.
</slate_header>
 */


package slatekit.examples


//<doc:import_required>
import slatekit.common.Field
//</doc:import_required>

//<doc:import_examples>
import slatekit.results.Try
import slatekit.results.Success
import slatekit.db.Db
import slatekit.common.db.DbConString
import slatekit.core.cmds.Cmd
import slatekit.entities.*
import slatekit.entities.repos.EntityRepoInMemoryWithLongId
import slatekit.meta.models.ModelMapper
import slatekit.orm.OrmMapper
import slatekit.orm.databases.vendors.MySqlConverter
import slatekit.orm.databases.vendors.MySqlEntityRepo

//</doc:import_examples>


class Example_Entities_Service : Cmd("service") {

    //<doc:setup>
    // Example entity that the Repo with manage via CRUD operations

    // Example entity that the Repo with manage via CRUD operations
    data class User(
            override val id: Long = 0L,


            @property:Field(required = true, length = 30)
            val email: String = "",


            @property:Field(required = true, length = 30)
            val firstName: String = "",


            @property:Field(required = true, length = 30)
            val lastName: String = "",


            @property:Field(required = true)
            val isMale: Boolean = false,


            @property:Field(required = true)
            val age: Int = 35

    ) : EntityWithId<Long>, EntityUpdatable<Long, User> {

        override fun isPersisted(): Boolean = id > 0

        /**
         * sets the id on the entity and returns the entity with updated id.
         * @param id
         * @return
         */
        override fun withId(id: Long): User = copy(id = id)
    }

    // First setup the database
    val db = Db(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/user_db", "root", "abcdefghi"))
    val model = ModelMapper.loadSchema(User::class)
    val mapper = OrmMapper<Long, User>(model, db, Long::class, MySqlConverter())

    // CASE 1: In-memory ( non-persisted ) repository has limited functionality
    // but is very useful for rapid prototyping of a data model when you are trying to
    // figure out what fields/properties should exist on the model
    val repo = EntityRepoInMemoryWithLongId<User>(User::class)

    // CASE 1A: Explicitly setup the type of id ( long ) and mapper to use
    val repoSetup2 = EntityRepoInMemoryWithLongId<User>(User::class)

    // CASE 2: My-sql ( persisted ) repository can be easily setup
    // More examples of database setup/entity registration available in Setup/Registration docs.

    // Now create the repo with database and mapper
    val repoMySql = MySqlEntityRepo<Long, User>(db, User::class, Long::class, mapper)

    // CASE 3: You can also extend from EntityRepositoryMySql
    class UserService(repo: EntityRepo<Long, User>) : EntityService<Long, User>(Entities({ con -> Db(con) }), repo)
    //</doc:setup>


    override protected fun executeInternal(args: Array<String>?): Try<Any> {

        //<doc:examples>
        // The Service layer initialized with an repository .
        // Purpose of the service layer is to:
        //
        // 1. Delegate to underlying repository for CRUD operations after applying any business logic.
        // 2. Provide a layer to insert business logic during other operations.
        // 3. Has some( not all ) methods that match the repository CRUD, Find, Delete methods
        // 4. Subclassed to perform more complex business logic that may still involve using the repo.
        //
        val service = UserService(repo)

        // CASE 1: Create 3-4 users for showing use-cases
        service.create(User(firstName = "Indiana", lastName = "doe-01"))
        service.create(User(firstName = "jane"   , lastName = "doe-02"))
        service.create(User(firstName = "john"   , lastName = "doe-03"))
        service.create(User(firstName = "jane"   , lastName = "doe-04"))

        // CASE 2: Get by id
        printOne("2", service.get(2))

        // CASE 3: Update
        val item2 = service.get(2)
        item2?.let { item ->
            val updated = item.copy(firstName = "user_two")
            service.update(updated)
        }

        // CASE 4: Get all
        printAll("all", service.getAll())

        // CASE 5: Get recent users ( 03, 04 )
        printAll("recent", service.recent(2))

        // CASE 6: Get oldest users ( 01, 02 )
        printAll("oldest", service.oldest(2))

        // CASE 7: Get first one ( oldest - 01 )
        printOne("first", service.first())

        // CASE 8: Get last one ( recent - 04 )
        printOne("last", service.last())

        // CASE 9: Delete by id
        service.deleteById(4)

        // CASE 10: Get total ( 4 )
        println(service.count())

        //</doc:examples>
        return Success("")
    }


    fun printAll(tag: String, models: List<User>): Unit {
        println()
        println(tag.toUpperCase())
        for (model in models)
            printOne(null, model)
    }


    fun printOne(tag: String?, model:User?): Unit {
        tag?.let { t ->
            println()
            println(t.toUpperCase())
        }

        model?.let { m ->
            println("User: " + m.id + ", " + m.firstName + ", " + m.lastName)
        }
    }
    /*
//<doc:output>
```bat
    2
    User: 2, jane, doe-02

    ALL
    User: 1, john, doe-01
    User: 3, john, doe-03
    User: 4, jane, doe-04
    User: 2, user_two, doe-02

    RECENT
    User: 4, jane, doe-04
    User: 3, john, doe-03

    OLDEST
    User: 1, john, doe-01
    User: 2, user_two, doe-02

    FIRST
    User: 1, john, doe-01

    LAST
    User: 4, jane, doe-04
    3
```
//</doc:output>
*/
}
