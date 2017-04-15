---
layout: start_page
title: module Orm-Setup
permalink: /mod-orm-setup
---

# Orm-Setup

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A registration system for entities and their corresponding repository/service impelementations | 
| **date**| 2017-04-12T22:59:15.514 |
| **version** | 1.4.0  |
| **jar** | slate.entities.jar  |
| **namespace** | slate.common.entities  |
| **source core** | slate.common.entities.Entities.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/entities](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/entities)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Entities_Reg.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Entities_Reg.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.databases._
import slate.common.results.ResultSupportIn
import slate.entities.core._
import slate.examples.common._
import slate.examples.common.User
import scala.reflect.runtime.universe._


// optional 
import slate.common.{Result}
import slate.core.cmds.Cmd


```

## Setup
```scala


    // The entities are dependent on the database connections setup.
    // See Example_Database.scala for more info

    // 1. Register the default connection
    val dbs = DbLookup.defaultDb(DbUtils.loadFromUserFolder(".slate", "db_default.txt"))

    // 2. Register a named connection
    //val dbs = DbLookup.namedDbs(("user_db", DbUtils.loadFromUserFolder(".slate", "db_default.txt"))

    // 3: Register connection as a shard and link to a group
    //val dbs = DbLookup.groupedDbs(("group1", List[(String,DbConString)](("shard1", DbUtils.loadFromUserFolder(".slate", "db_default.txt")))))
    

```

## Usage
```scala


    // The entities can be registered and set up in multiple ways.
    // They can be registered to :
    //
    // - use in-memory repository or a sql ( mysql ) repository
    // - use the default EntityService[T] or a custom EntityService
    // - use a singleton instance or new instance
    // - use a certain type of database ( mysql only for now )
    // - use the default EntityRepository ( mysql ) or a custom repository
    // - use a supplied EntityMapper or a custom mapper
    val entities = new Entities(Some(dbs))

    // Case 1: In-memory
    showResults( "Case 1", entities.register[User](isSqlRepo= false, entityType = typeOf[User]))

    // Case 2: In-memory + with custom service
    showResults( "Case 2", entities.register[User](isSqlRepo= false, entityType = typeOf[User],
      serviceType= Some(typeOf[UserService])))

    // Case 3: Sql-repo = EntityRepository[T] - mysql, default service ( EntityService[T] )
    // Note: this uses the default database connection above
    showResults( "Case 3", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      dbType = Some(DbTypeMySql)))

    // Case 4: Sql-repo + with custom service = default sql repo ( EntityRepository[T] - mysql )
    // Note: this uses the default database connection above
    showResults( "Case 4", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= Some(typeOf[UserService]), dbType = Some(DbTypeMySql)))

    // Case 5: Custom repository
    // Note: this uses the default database connection above
    showResults( "Case 5", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      repository= Some(new UserRepository(typeOf[User])), dbType = Some(DbTypeMySql)))

    // Case 6: Custom service type, custom repository, database type
    showResults( "Case 6", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= Some(typeOf[UserService]), repository= Some(new UserRepository(typeOf[User])), dbType= Some(DbTypeMySql)))

    // Case 7: Custom service type, custom repository, database specified
    // Note: this uses the named database connection above called "user_db"
    showResults( "Case 7", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= Some(typeOf[UserService]), repository= Some(new UserRepository(typeOf[User])),
      dbType= Some(DbTypeMySql), dbKey = Some("user_db")))

    // Case 8: Custom service type, custom repository, database specified, mapper specified
    // Each registration will simply overwrite an existing registration for the same entity type
    showResults( "Case 8", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= Some(typeOf[UserService]), repository= Some(new UserRepository(typeOf[User])),
      mapper= None, dbType= Some(DbTypeMySql)))

    // Case 9: Provide a database db key ( e.g. for multiple database connections )
    showResults( "Case 9", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= Some(typeOf[UserService]), repository= Some(new UserRepository(typeOf[User])),
      mapper= None, dbType= Some(DbTypeMySql), dbKey = Some("user_db")))

    // Case 9: Provide a database db key ( e.g. for multiple database connections )
    showResults( "Case 10", entities.register[User](isSqlRepo= true, entityType = typeOf[User],
      serviceType= Some(typeOf[UserService]), repository= Some(new UserRepository(typeOf[User])),
      mapper= None, dbType= Some(DbTypeMySql), dbKey = Some("group1"), dbShard = Some("shard1")))
    //</doc:setup>

    //<doc:examples>
    // Use case 1: Get repository
    val repo = entities.getRepo[User]()

    // Use case 2: Get the service
    val svc = entities.getSvc[User]()

    // Use case 3: Get the entity mapper
    val mapper = entities.getMapper[User]()

    // Use case 4: Get the repo for a specific shard
    val repoShard = entities.getRepo[User]()
    

```

