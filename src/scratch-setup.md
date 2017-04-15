---
layout: start_page
title: module Utils
permalink: /scratch-setup
---


## Libs
Ensure all the dependent jars are in the lib folder of your application/library.

```scala 

 C:\apps\MySampleApp\lib\slate-cloud_2.11-1.0.jar   
 C:\apps\MySampleApp\lib\slate-common_2.11-1.0.jar  
 C:\apps\MySampleApp\lib\slate-core_2.11-1.0.jar    
 C:\apps\MySampleApp\lib\slate-entities_2.11-1.0.jar
 C:\apps\MySampleApp\lib\slate-ext_2.11-1.0.jar     
 C:\apps\MySampleApp\lib\slate-tools_2.11-1.0.jar 
 
 C:\apps\MySampleApp\lib\scala-library.jar  
 C:\apps\MySampleApp\lib\scala-reflect.jar 
 
 C:\apps\MySampleApp\lib\config-1.2.1.jar                    
 C:\apps\MySampleApp\lib\json_simple-1.1.jar                 
 C:\apps\MySampleApp\lib\mysql-connector-java-5.1.38-bin.jar 
 
```

## Compile 
Open a command window and move to the root directory of your app 

```scala

C:\apps\MySampleApp>sbt compile

```

## Run 

```scala

C:\apps\MySampleApp>sbt run

```

## Package

```scala

C:\apps\MySampleApp>sbt package

```


```props

c:\apps\MySampleApp\target\scala-2.11\mysampleapp.jar

```

## Using Scala

```scala 

// Include all your jars here!
scala -classpath "slate-common_2.11-1.0.jar;...;mysampleapp.jar" mycompany.apps.MySampleApp

```

## Using Java

```scala 

// Include all your jars here!
java -cp slate-common_2.11-1.0.jar;...;mysamplepp.jar; mycompany.apps.MySampleApp

```

## Using Java Manifest
To package an app for deployment, it may be easier to pacakge your app using a manifest file

```scala 

// NOTES: This example is using windows commands 
// 1. Package up your app using sbt package above 
// C:\apps\MySampleApp>sbt package

// 2. Copy classes over from app\target to some temp directory
C:\apps\MySampleApp>xcopy /e /v /y target\scala-2.11\classes c:\apps\temp\

// 3. move to temp to package everything using jar and manifest
C:\apps\MySampleApp>cd c:\apps\temp\

// 4. jar all the files with custom manifest
c:\apps\temp>jar -cvfm mysampleapp.jar c:\apps\build\manifest.txt *

// 5. Copy the built jar into a final destination directory 
c:\apps\temp>copy mysampleapp.jar c:\apps\dist\mysampleapp.jar 

// NOTE!! Ensure all the other dependent libraries from the manifest are also
// in this destination directory.

```

The manifest file will look something like this.

```props
Manifest-Version: 1.0
Main-Class: mycompany.apps.MySampleApp
Class-Path: config-1.2.1.jar
  json_simple-1.1.jar
  mysql-connector-java-5.1.38-bin.jar
  scala-library.jar
  scala-reflect.jar
  slate-common_2.11-1.0.jar
  slate-entities_2.11-1.0.jar
  slate-core_2.11-1.0.jar
  slate-integration_2.11-1.0.jar
  slate-cloud_2.11-1.0.jar
```

Now you can simply run your app using the jar.

```scala 

c:\apps\dist>java -jar mysamplepp.jar

```

# Apis
The apis in Slate Kit are built to be **protocol independent**. 
This means that you can build your APIs 1 time and they can be made to be available on the command line shell or as a Web API, or both. This is done using various techniques outlined below. Before going into the details, first review the terminology.

# Databases 
```scala 
    
  import slate.common.databases.{DbConfig, DbLookup, DbConstants}

  // 1. Register the default connection
  DbLookup.setDefault("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mydb", "root", "123456789")

  // 2. Register using conf file from use folder {user}/.slate/db.txt
  // NOTE: This is a more secure
  DbLookup.setDefault( DbConfig.loadFromUserFolder(".slate", "db.txt") )
    
  // 3. Create a new db connection string
  new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mydb", "root", "123456789")
  
  // 4. Register named connections 
  DbLookup.set("user_db1", new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mydb", "root", "123456789"))
  DbLookup.set("user_db2", DbConfig.loadFromUserFolder(".slate", "db.txt") )

```


# Entitites
```scala 

  import slate.entities.core._

  // 1. Extend from IEntity
  // 2. Add @Field annotations for fields that should be mapped
  class User extends IEntity
  {
      // Primary key
      var id = 0L
  
      @Field("", true, 50)
      var userName  = ""
  
      @Field("", true, 50)
      var email  = ""
  
      @Field("", true, 20)
      var password  = ""
  
      // The timestamp and audit fields
      @Field("", true, -1)
      var createdAt  = DateTime.now()
  
      @Field("", true, -1)
      var createdBy  = 0
  
      @Field("", true, -1)
      var updatedAt  =  DateTime.now()
  
      @Field("", true, -1)
      var updatedBy  = 0
  }

```


# Service
```scala 

  import slate.entities.core._

  // PREREQUISITE: Setup the default database connection.
  // See database setup for more info
  DbLookup.setDefault("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mydb", "root", "123456789")
  
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
  val entities = new Entities() 
  entities.register[User]( isSqlRepo= true, 
                           entityType = typeOf[User], 
                           serviceType= typeOf[UserService], 
                           dbType = DbConstants.DbMySql)
    
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

  // PREREQUISITE: Setup the default database connection.
  // See database setup for more info
  DbLookup.setDefault("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mydb", "root", "123456789")
  
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
  val entities = new Entities() 
  entities.register[User]( isSqlRepo= true, 
                           entityType = typeOf[User], 
                           serviceType= typeOf[UserService], 
                           repository= new UserRepository(typeOf[User]), 
                           dbType= "mysql" 
                         )
  
  // =====================================================
  // SETUP 4: Manual configuration of db, mapper, repo.
  // Create the database config 
  val dbConfig = new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mydb", "root", "123456789")
  
  // Create the mapper to map entity to/from SQL
  val mapper = new EntityMapper(null)
  mapper.loadSchema(new User(), typeOf[User]) 

  // Set db and mapper on the repo  
  repoMySql.setDb(dbConfig)
  repoMySql.setMapper(mapper)
  
```

# Setup
```scala 

val entities = new Entities()

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

  import slate.entities.core._
  
  // STEP 1: Create instance of entity class
  val user = new User()

  // STEP 2: Create a new mapper that loads fields/annotations mappings from the entity class
  val mapper = new EntityMapper(null)
  val modelSchema = mapper.loadSchema(person, typeOf[Consultant])

  // STEP 3: Create instance for testing
  user.firstName = "john"
  user.lastName = "doe"
  user.email = "john.doe@gmail.com"

  // STEP 4: Get the sql for create
  val sqlCreate = mapper.mapToSql(person, update = false, fullSql = true)
  println(sqlCreate)

  // STEP 5: Get the sql for update
  val sqlForUpdate = mapper.mapToSql(person, update = true, fullSql = true)
  println(sqlForUpdate)

  // STEP 6: Generate the table schema for mysql from the model schema
  println( "table sql : " + new DbBuilder().addTable(modelSchema))
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



