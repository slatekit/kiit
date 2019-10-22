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
import slatekit.common.*
import slatekit.common.db.DbCon
import slatekit.meta.models.Model
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.db.Db
import slatekit.entities.EntityWithId
import slatekit.entities.core.EntityInfo
import slatekit.examples.common.User
import slatekit.meta.models.ModelMapper
import slatekit.orm.OrmMapper
import slatekit.orm.databases.vendors.MySqlBuilder
import slatekit.orm.databases.vendors.MySqlConverter

//</doc:import_required>

//<doc:import_examples>
import slatekit.results.Try
import slatekit.results.Success
//</doc:import_examples>


class Example_Mapper : Command("mapper") {

    //<doc:setup>
    data class Movie(
            override val id :Long = 0L,


            @property:Field(required = true, length = 50)
            val title :String = "",


            @property:Field(length = 20)
            val category :String = "",


            @property:Field(required = true)
            val playing :Boolean = false,


            @property:Field(required = true)
            val cost:Int,


            @property:Field(required = true)
            val rating: Double,


            @property:Field(required = true)
            val released: DateTime,


            // These are the timestamp and audit fields.
            @property:Field(required = true)
            val createdAt : DateTime = DateTime.now(),


            @property:Field(required = true)
            val createdBy :Long  = 0,


            @property:Field(required = true)
            val updatedAt : DateTime =  DateTime.now(),


            @property:Field(required = true)
            val updatedBy :Long  = 0
    )
        : EntityWithId<Long>
    {

        override fun isPersisted(): Boolean = id > 0

        companion object {
            fun samples():List<Movie> = listOf(
                    Movie(
                            title = "Indiana Jones: Raiders of the Lost Ark",
                            category = "Adventure",
                            playing = false,
                            cost = 10,
                            rating = 4.5,
                            released = DateTimes.of(1985, 8, 10)
                    ),
                    Movie(
                            title = "WonderWoman",
                            category = "action",
                            playing = true,
                            cost = 100,
                            rating = 4.2,
                            released = DateTimes.of(2017, 7, 4)
                    )
            )
        }
    }
    //</doc:setup>


    override fun execute(request: CommandRequest) : Try<Any>
    {
        //<doc:examples>
        // NOTE: There are 3 different ways to load the schema of the entity.
        // 1. automatically using annotations
        // 2. manually using properties references
        // 3. manually using methods and string names


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
        val schema3 = Model(Movie::class)
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


        // CASE 4: Now with a schema of the entity, you create a mapper
        val mapper = OrmMapper<Long, User>(schema1, Db(DbCon.empty), MySqlConverter(), EntityInfo(Long::class, User::class, "users"))

        // Create sample instance to demo the mapper
        val movie = Movie(
                        title = "Man Of Steel",
                        category = "action",
                        playing = false,
                        cost = 100,
                        rating = 4.0,
                        released = DateTimes.of(2015, 7, 4)
                )

        // CASE 5: Get the sql for create
        val sqlCreate = mapper.mapFields(null, movie, schema1, false)
        println(sqlCreate)

        // CASE 6: Get the sql for update
        val sqlForUpdate = mapper.mapFields(null, movie, schema1, true)
        println(sqlForUpdate)

        // CASE 7: Generate the table schema for mysql from the model
        println("table sql : " + MySqlBuilder(null).createTable(schema1))
        //</doc:examples>

        return Success("")
    }
}
