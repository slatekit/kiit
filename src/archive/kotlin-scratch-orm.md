---
layout: start_page
title: module Utils
permalink: /kotlin-scratch-orm
---

# Databases
```kotlin

import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.db.DbLookup.DbLookupCompanion.namedDbs
import slatekit.common.db.DbTypeMySql


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
```


# Entities
```kotlin

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.entities.core.EntityWithId
 
// NOTES: You can entities properties to be persisted
// in 3 different ways:
// 1. annotations on the Entity
// 2. building a Model and manually registering property references
// 3. building a Model and manually registering via field/names
//
// See Example_Mapper.kt and slatekit.common.Model for more info.
//
// IMMUTABILITY:
// The ORM is originally built for immutable Entities ( Data Classes )
// It also supports Entities with "vars", but has not been tested.
// In a future release, we will fully support var properties
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
    : EntityWithId
{
    companion object {
        fun samples():List<Movie> = listOf(
                Movie(
                        title = "Indiana Jones: Raiders of the Lost Ark",
                        category = "Adventure",
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTime.of(1985, 8, 10)
                ),
                Movie(
                        title = "WonderWoman",
                        category = "action",
                        playing = true,
                        cost = 100,
                        rating = 4.2,
                        released = DateTime.of(2017, 7, 4)
                )
        )
    }
}
```


# Registration
```kotlin

import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.entities.core.Entities

 val dbLookup = defaultDb(
            DbConString(
                "com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost/default",
                "root",
                "abcdefghi"
            )
        )
		
// The entities can be registered and set up in multiple ways.
// They can be registered to :
//
// - use in-memory repository or a sql ( mysql ) repository
// - use the default EntityService[T] or a custom EntityService
// - use a singleton instance or new instance
// - use a certain type of database ( mysql only for now )
// - use the default EntityRepository ( mysql ) or a custom repository
// - use a supplied EntityMapper or a custom mapper
val entities = Entities(dbLookup)

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
		serviceType = MovieService::class, repository = MovieRepository (), dbType = DbTypeMySql))

// Case 7: Custom service type, custom repository, database specified
// Note: this uses the named database connection above called "Movie_db"
showResults("Case 7", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
		serviceType = MovieService::class, repository = MovieRepository (),
		dbType = DbTypeMySql, dbKey = "Movie_db"))

// Case 8: Custom service type, custom repository, database specified, mapper specified
// Each registration will simply overwrite an existing registration for the same entity type
showResults("Case 8", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
		serviceType = MovieService::class, repository = MovieRepository (),
		dbType = DbTypeMySql))

// Case 9: Provide a database db key ( e.g. for multiple database connections )
showResults("Case 9", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
		serviceType = MovieService::class, repository = MovieRepository (),
		dbType = DbTypeMySql, dbKey = "Movie_db"))

// Case 9: Provide a database db key ( e.g. for multiple database connections )
showResults("Case 10", entities.register<Movie>(isSqlRepo = true, entityType = Movie::class,
		serviceType = MovieService::class, repository = MovieRepository (),
		dbType = DbTypeMySql, dbKey = "group1", dbShard = "shard1"))

// Use case 1: Get repository
val repo = entities.getRepo<Movie>(Movie::class)

// Use case 2: Get the service
val svc = entities.getSvc<Movie>(Movie::class)

// Use case 3: Get the entity mapper
val mapper = entities.getMapper(Movie::class)

// Use case 4: Get the repo for a specific shard
val repoShar = entities.getRepo<Movie>(Movie::class)
        
```


# Service: Declare 
```kotlin

import slatekit.core.common.AppContext
import slatekit.core.common.EntityServiceWithSupport
import slatekit.entities.core.EntityRepo


class MovieService(context: AppContext, repo: EntityRepo<Movie>)
  : EntityServiceWithSupport<Movie>(context, repo)
{
}
```


# Service: Use 
```kotlin
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
service.delete(4)

// CASE 10: Get total ( 4 )
println(service.count())

// CASE 11: Type-Safe queryusing property type reference
println(service.findBy(Movie::playing, true))

// CASE 12: Query
println(service.find(Query().where("playing", "=", true)))

```


# repo: declare
```kotlin

import slatekit.common.db.Db
import slatekit.common.db.DbConString
import slatekit.common.mapper.Mapper
import slatekit.entities.core.Entities
import slatekit.entities.core.EntityMapper
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.entities.repos.EntityRepoMySql

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
val model = Mapper.loadSchema(Movie::class)
val mapper = EntityMapper(model)

// 2.3: Now create the repo with database and mapper
val repoMySql = EntityRepoMySql<Movie>(db, Movie::class, null, mapper)

```



# repo: usage 
```kotlin

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

```


# mapper: setup 
```kotlin

import slatekit.common.Model
import slatekit.common.mapper.Mapper

// CASE 1: Load the schema from the annotations on the model
val schema1 = Mapper.loadSchema(Movie::class)


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
		
```

# mapper: usage 
```kotlin 

import slatekit.entities.core.EntityMapper

val schema = Mapper.loadSchema(Movie::class)

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
println("table sql : " + DbSourceMySql().builAddTable(schema))

```