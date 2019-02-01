---
layout: start_page_mods_utils
title: module Orm-Repo
permalink: /kotlin-mod-orm-repo
---

# Orm-Repo

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A repository pattern for entity/model CRUD operations | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.entities.jar  |
| **namespace** | slatekit.common.entities  |
| **source core** | slatekit.common.entities.EntityRepo.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Entities_Repo.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Entities_Repo.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.common.Field
import slatekit.entities.core.*
import slatekit.entities.repos.EntityRepoInMemory
import slatekit.entities.repos.EntityRepoMySql
import slatekit.common.Mapper


// optional 
import slatekit.common.Result
import slatekit.common.db.Db
import slatekit.common.db.DbConString
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd
import slatekit.meta.models.ModelMapper



```

## Setup
```kotlin


    // Example entity that the Repo with manage via CRUD operations
    data class User(
            override val id:Long = 0L,


            @property:Field(required = true, length = 30)
            val email:String = "",


            @property:Field(required = true, length = 30)
            val firstName:String = "",


            @property:Field(required = true, length =30)
            val lastName:String = "",


            @property:Field(required = true)
            val isMale:Boolean = false,


            @property:Field(required = true)
            val age:Int = 35

    ) : EntityWithId {


        fun fullname():String = firstName + " " + lastName



        override fun toString():String  {
            return "$email, $firstName, $lastName, $isMale, $age"
        }
    }

    // CASE 1: In-memory ( non-persisted ) repository has limited functionality
    // but is very useful for rapid prototyping of a data model when you are trying to
    // figure out what fields/properties should exist on the model
    val repo = EntityRepoInMemory<User>(User::class)

    // CASE 2: My-sql ( persisted ) repository can be easily setup
    // More examples of database setup/entity registration available in Setup/Registration docs.
    // 1. First setup the database
    val db = Db(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/user_db", "root", "abcdefghi"))

    // 2. Setup the mapper
    val model = ModelMapper.loadSchema(User::class)
    val mapper = EntityMapper(model)


    // 3. Now create the repo with database and mapper
    val repoMySql = EntityRepoMySql<User>(db, User::class, null, mapper)

    // CASE 3: You can also extend from EntityRepositoryMySql
    class UserRepository(db:Db, mapper:Mapper) : EntityRepoMySql<User>(db, User::class)


    val userRepo = UserRepository(db, mapper)
    

```

## Usage
```kotlin


        // CASE 1: Create 3-4 users for showing use-cases
        repo.create(User(firstName ="john", lastName = "doe-01"))
        repo.create(User(firstName ="jane", lastName = "doe-02"))
        repo.create(User(firstName ="john", lastName = "doe-03"))
        repo.create(User(firstName ="jane", lastName = "doe-04"))

        // CASE 2: Get by id
        printOne("2", repo.get(2))

        // CASE 3: Update
        val item2 = repo.get(2)
        item2?.let { item ->
            val updated = item.copy(firstName = "user_two")
            repo.update(updated)
        }


        // CASE 4: Get all
        printAll("all", repo.getAll())

        // CASE 5: Get recent users ( 03, 04 )
        printAll("recent", repo.recent(2))

        // CASE 6: Get oldest users ( 01, 02 )
        printAll("oldest", repo.oldest(2))

        // CASE 7: Get first one ( oldest - 01 )
        printOne("first", repo.first())

        // CASE 8: Get last one ( recent - 04 )
        printOne("last", repo.last())

        // CASE 9: Delete by id
        repo.delete(4)

        // CASE 10: Get total ( 4 )
        println(repo.count())

        

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
