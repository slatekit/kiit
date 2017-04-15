---
layout: start_page
title: module Utils
permalink: /scratch-orm
---

# Apis
The apis in Slate Kit are built to be **protocol independent**. 
This means that you can build your APIs 1 time and they can be made to be available on the command line shell or as a Web API, or both. This is done using various techniques outlined below. Before going into the details, first review the terminology.

# Databases 
```scala 
    
  import slate.common.databases.DbConfig
  import slate.common.databases.DbLookup._
  
    // CASE 1: Load database lookup using a single db connection
    val dbLookup1 = defaultDb(
      new DbConString (
        "com.mysql.jdbc.Driver",
        "jdbc:mysql://localhost/default",
        "db1",
        "abcdefghi"
      )
    )
    showResult( dbLookup1.default )

    // CASE 2. Load database lookup using a single db connection
    // from a config file from .slatekit folder 
    val dbLookup2 = defaultDb( DbUtils.loadFromUserFolder(".slatekit", "db_default.txt"))
    showResult( dbLookup2.default )

    // CASE 3: Load database lookup using named db connection "users"
    val dbLookup3 = namedDbs( ("users", DbUtils.loadFromUserFolder(".slate", "db_default.txt") ))
    showResult( dbs4.named("users") )

    // CASE 4: Load database lookup using multiple 
	// named db connections ( "users", "files" )
    val dbs4 = namedDbs(
      (
        "users", new DbConString (
          "com.mysql.jdbc.Driver",
          "jdbc:mysql://localhost/users",
          "root",
          "abcdefghi"
        )
      ),
      (
        "files", new DbConString (
          "com.mysql.jdbc.Driver",
          "jdbc:mysql://localhost/files",
          "root",
          "abcdefghi"
        )
      )
    )
    showResult( dbs4.named("users") )
    showResult( dbs4.named("fiels") )
```


# Entitites
```scala 

  import slate.entities.core._

  // 1. Extend from IEntity
  // 2. Add @Field annotations for fields that should be mapped
  case class User (

      // Primary key
      @(Field@field)("", true, -1)
      id : Long,
  
      @(Field@field)("", true, 50)
      userName: String,
  
      @(Field@field)("", true, 50)
      email: String,
  
      @(Field@field)("", true, 20)
      password: String,
	  
      @(Field@field)("", true, -1)
      createdAt: DateTime,
  
      @(Field@field)("", true, -1)
      createdBy:Long,
  
      @(Field@field)("", true, -1)
      updatedAt: DateTime,
  
      @(Field@field)("", true, -1)
      updatedBy:Long,

      @(Field@field)("", true, -1)
      uniqueId: String	  
  )
   extends EntityWithId
      with EntityWithMeta
      with EntityUpdatable[User]
  {
	/**
      * support for "copy on write" to set the id on the entity and return a copy.
      * @param id
      * @return
      */
    override def withId(id:Long): User = copy(id = id)
  }

```


# Service
```scala 

  import slate.entities.core._
  import slate.common.databases.DbConfig
  import slate.common.databases.DbLookup._
  

  
  // Create database connection lookup
  // single connection in this example
  val dbLookup = defaultDb(
        new DbConString (
          "com.mysql.jdbc.Driver",
          "jdbc:mysql://localhost/default",
          "db1",
          "abcdefghi"
        )
      )
	  
  // =====================================================
  // SETUP 1: Use the existing EntityService[T] class
  val svc1 = new EntityService[User](getUserRepo())
  
  
  // =====================================================
  // SETUP 2: Create your own service to avoid generics
  class UserService(repo:EntityRepo[User]) extends EntityService[User](repo)
  {
  }  
  var svc = new UserService(getUserRepo())
  
  
  // =====================================================
  // SETUP 3: This is the most convenient way to wire up all the components.
  // Entities is a registration system for your entities.
  // a) database
  // b) entity
  // c) service 
  val entities = new Entities(Some(dbLookup)) 
  entities.register[User]( isSqlRepo= true, 
                           entityType = typeOf[User], 
                           serviceType= typeOf[UserService], 
                           dbType = Some(DbTypeMySql))
    
  // EXAMPLES: 
  // 1. Create 
  svc.create(new User("user", "04"))
  
  // 2. Retrieve  
  val item = svc.get( 2 )
  svc.getAll()
  
  // 3. Update
  item.get.firstName = "user_two"
  svc.update(item2.get)
  
  // 4. Delete
  svc.delete( 4 )
  
  // 5. Find by date
  svc.recent(2)
  svc.oldest(2)
  svc.first()
  svc.last()
  svc.count()
  
```



# Repo
```scala 

  import slate.entities.repos._  
  import slate.common.mapper.Mapper
  import slate.common.databases.DbConfig
  import slate.common.databases.DbLookup._
  

  // Create database connection lookup
  // single connection in this example
  val dbLookup = defaultDb(
        new DbConString (
          "com.mysql.jdbc.Driver",
          "jdbc:mysql://localhost/default",
          "db1",
          "abcdefghi"
        )
      )
	  
  // =====================================================
  // SETUP 1: Use existing implementation of Repo for MySql 
  val repoMySql = new EntityRepoMySql[User](typeOf[User])
  
  // =====================================================
  // SETUP 2: Create your own repository for customization
  class UserRepository() extends EntityRepoMySql[User](typeOf[User])
  {
  }
  
  // =====================================================
  // SETUP 3: This is the most convenient way to wire up all the components.
  // Entities is a registration system for your entities.
  // a) database
  // b) entity
  // c) service 
  val entities = new Entities(Some(dbLookup)) 
  entities.register[User]( isSqlRepo= true, 
                           entityType = typeOf[User], 
                           serviceType= typeOf[UserService], 
                           repository= new UserRepository(typeOf[User]), 
                           dbType= Some(DbTypeMySql)
                         )
  
  // =====================================================
  // SETUP 4: Manual configuration of db, mapper, repo.
  // Create the database config 
  val dbConfig = new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mydb", "root", "123456789")
  
  // Create the mapper to map entity to/from SQL
  val model =  Mapper.loadSchema(new User(), typeOf[User]) 
  val mapper = new EntityMapper(model)

  // Set db and mapper on the repo  
  repoMySql.setDb(dbConfig)
  repoMySql.setMapper(mapper)
  
```

# Setup
```scala 

  import slate.common.databases.DbConfig
  import slate.common.databases.DbLookup._
  
// Create database connection lookup
// single connection in this example
val dbLookup = defaultDb(
      new DbConString (
        "com.mysql.jdbc.Driver",
        "jdbc:mysql://localhost/default",
        "db1",
        "abcdefghi"
      )
    )

// Entities stores all the entity registrations 
val entities = new Entities(Some(dbLookup))

// Set up 1: Simple ( using default service, repo, connection ) 
entities.register[User](isSqlRepo= true, entityType = typeOf[User], dbType = DbConstants.DbMySql)

// Set up 2: Custom service, default repo, connection 
entities.register[User](isSqlRepo= true, entityType = typeOf[User], serviceType= typeOf[UserService], dbType = DbConstants.DbMySql)

// Set up 3: Custom service, custom repo, default connection 
entities.register[User](isSqlRepo= true, entityType = typeOf[User], serviceType= typeOf[UserService], repository= new UserRepository(typeOf[User]), dbType = DbConstants.DbMySql)

// Set up 4: Custom service, repo and connection
entities.register[User](isSqlRepo= true, entityType = typeOf[User], serviceType= typeOf[UserService], repository= new UserRepository(typeOf[User]), dbType = DbConstants.DbMySql, dbKey = "user_db")

// Use case 1: Get repository
val repo = entities.getRepo(typeOf[User])

// Use case 2: Get the service
val svc = entities.getService(typeOf[User])

// Use case 3: Get the entity mapper
val mapper = entities.getMapper(typeOf[User])

// Use case 4: Get the repo for a specific shard
val repoShard = entities.getRepo(typeOf[User])
	
```


# Mapper
```scala 

  
  import slate.common.mapper.Mapper
  import slate.entities.core._
  
  // STEP 1: Create a new mapper that loads fields/annotations mappings from the entity class
  val model = Mapper.loadSchema(typeOf[User])
  val mapper = new EntityMapper(model)
  
  // STEP 2: Create instance of entity class
  val user = new User(firstName ="john", lastName = "doe-01", email = "john@abc.com")

  // STEP 3: Get the sql for create
  val sqlCreate = mapper.mapToSql(person, update = false, fullSql = true)
  println(sqlCreate)

  // STEP 4: Get the sql for update
  val sqlForUpdate = mapper.mapToSql(person, update = true, fullSql = true)
  println(sqlForUpdate)

  // STEP 5: Generate the table schema for mysql from the model schema
  println( "table sql : " + new DbBuilder().addTable(model))
```



# Models
```scala 

  import slate.common.{Model}
  
  val model = new Model("Resource", "slate.ext.resources.Resource")

  // CASE 2. add a field for uniqueness / identity
  model.addId  (name = "id", autoIncrement = true, dataType = typeOf[Long])

  // CASE 3: add fields for text, bool, int, date etc.
  model.addText  (name = "key"     , isRequired = true, maxLength = 30)
       .addText  (name = "name"       , isRequired = true, maxLength = 30)
       .addText  (name = "category"   , isRequired = true, maxLength = 30)
       .addText  (name = "country"    , isRequired = true, maxLength = 30)
       .addText  (name = "region"     , isRequired = true, maxLength = 30)
       .addText  (name = "aggRegion"  , isRequired = true, maxLength = 30)
       .addText  (name = "aggCategory", isRequired = true, maxLength = 30)
       .addText  (name = "links"      , isRequired = true, maxLength = 30)
       .addText  (name = "owner"      , isRequired = true, maxLength = 30)
       .addText  (name = "status"     , isRequired = true, maxLength = 30)
       .addInt   (name = "recordState", isRequired = true)
       .addObject(name = "hostInfo"   , isRequired = true, dataType = typeOf[Host])
       .addDate  (name = "created"    , isRequired = true                )
       .addDate  (name = "updated"    , isRequired = true                )
```



