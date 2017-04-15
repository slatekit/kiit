---
layout: start_page
title: module Orm-Repo
permalink: /mod-orm-repo
---

# Orm-Repo

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A repository pattern for entity/model CRUD operations | 
| **date**| 2017-04-12T22:59:15.492 |
| **version** | 1.4.0  |
| **jar** | slate.entities.jar  |
| **namespace** | slate.common.entities  |
| **source core** | slate.common.entities.EntityRepo.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/entities](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/entities)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Entities_Repo.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Entities_Repo.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.databases.{DbConString, Db}
import slate.common.mapper.Mapper
import slate.common.results.ResultSupportIn
import slate.entities.core._
import slate.entities.repos._
import scala.annotation.meta.field
import scala.reflect.runtime.universe.{typeOf,Type}


// optional 
import slate.common.{Field, DateTime, Result}
import slate.core.cmds.Cmd


```

## Setup
```scala


  // Example entity that the Repo with manage via CRUD operations
  case class User(

              @(Field@field)("", true, 30)
              id:Long = 0L,


              @(Field@field)("", true, 30)
              email:String = "",


              @(Field@field)("", true, 30)
              firstName:String = "",


              @(Field@field)("", true, 30)
              lastName:String = "",


              @(Field@field)("", true, 50)
              lastLogin:DateTime  = DateTime.now(),


              @(Field@field)("", true, -1)
              isEmailVerified:Boolean  = false,


              @(Field@field)("", true, -1)
              status:Int  = 0,


              @(Field@field)("", true, -1)
              createdAt:DateTime  = DateTime.now(),


              @(Field@field)("", true, -1)
              createdBy:Long  = 0,


              @(Field@field)("", true, -1)
              updatedAt:DateTime  =  DateTime.now(),


              @(Field@field)("", true, -1)
              updatedBy:Long  = 0,


              @(Field@field)("",true, 50)
              uniqueId: String = ""

              )
    extends EntityWithId
      with EntityWithMeta
      with EntityUpdatable[User]
  {
    /**
      * sets the id on the entity and returns the entity with updated id.
      * @param id
      * @return
      */
    override def withId(id:Long): User = copy(id = id)
  }

  // CASE 1: In-memory ( non-persisted ) repository has limited functionality
  // but is very useful for rapid prototyping of a data model when you are trying to
  // figure out what fields/properties should exist on the model
  val repo = new EntityRepoInMemory[User](typeOf[User])

  // CASE 2: My-sql ( persisted ) repository can be easily setup
  // More examples of database setup/entity registration available in Setup/Registration docs.
  // 1. First setup the database
  val db = new Db(new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/user_db", "root", "abcdefghi"))

  // 2. Setup the mapper
  var model = Mapper.loadSchema(typeOf[User])
  val mapper = new EntityMapper(model)


  // 3. Now create the repo with database and mapper
  val repoMySql = new EntityRepoMySql[User](typeOf[User], None, Some(mapper), Some("user"), db)

  // CASE 3: You can also extend from EntityRepositoryMySql
  class UserRepository()
    extends EntityRepoMySql[User](typeOf[User],entityMapper = Some(mapper), db = db)
  {
  }
  val userRepo = new UserRepository()
  

```

## Usage
```scala


    // CASE 1: Create 3-4 users for showing use-cases
    repo.create(new User(firstName ="john", lastName = "doe-01"))
    repo.create(new User(firstName ="jane", lastName = "doe-02"))
    repo.create(new User(firstName ="john", lastName = "doe-03"))
    repo.create(new User(firstName ="jane", lastName = "doe-04"))

    // CASE 2: Get by id
    printOne("2", repo.get( 2 ) )

    // CASE 3: Update
    val item2 = repo.get( 2 )
    val item2b = item2.get.copy(firstName = "user_two")
    repo.update(item2b)

    // CASE 4: Get all
    printAll("all", repo.getAll() )

    // CASE 5: Get recent users ( 03, 04 )
    printAll("recent", repo.recent(2) )

    // CASE 6: Get oldest users ( 01, 02 )
    printAll("oldest", repo.oldest(2) )

    // CASE 7: Get first one ( oldest - 01 )
    printOne("first", repo.first() )

    // CASE 8: Get last one ( recent - 04 )
    printOne("last", repo.last() )

    // CASE 9: Delete by id
    repo.delete( 4 )

    // CASE 10: Get total ( 4 )
    println( repo.count() )

    

```


## Output

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
  