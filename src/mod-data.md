---
layout: start_page_mods_utils
title: module Data
permalink: /kotlin-mod-data
---

# Data

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Database access utilty to query and manage data using JDBC for MySql. Other database support coming later. | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.db  |
| **source core** | slatekit.common.db.Db.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Database.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Database.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.db.Db
import slatekit.common.db.DbConString


// optional 
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd
import slatekit.entities.core.EntityMapper
import slatekit.examples.common.User
import slatekit.meta.createTable
import slatekit.meta.models.ModelMapper



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


        // NOTES:
        // 1. The Db.kt simply uses JDBC
        // 2. There is a separate DbLookup.kt component that
        //    loads, stores, and manages named database connections.
        //    Refer to that example for more info.

        // CASE 1: Create DB connection.
        val con = DbConString(
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://localhost/default",
            "root",
            "abcdefghi"
        )

        // CASE 2. Initialize the DB with the connection string.
        // NOTE: This defaults the db to mysql. The first line is same
        // as db = Db(con, source: DbSourceMySql())
        // In the future, we can more easily support mutliple databases
        // using this approach.
        val db = Db(con)

        // CASE 3: Open the database
        db.open()

        // CASE 4: Get scalar values
        val total1 = db.getScalarString       ("select test_string from db_tests where id = 1")
        val total2 = db.getScalarBool         ("select test_bool   from db_tests where id = 1")
        val total3 = db.getScalarShort        ("select test_short  from db_tests where id = 1")
        val total4 = db.getScalarInt          ("select test_int    from db_tests where id = 1")
        val total5 = db.getScalarLong         ("select test_long   from db_tests where id = 1")
        val total6 = db.getScalarDouble       ("select test_double from db_tests where id = 1")
        val total7 = db.getScalarLocalDate    ("select test_ldate  from db_tests where id = 1")
        val total8 = db.getScalarLocalTime    ("select test_ltime  from db_tests where id = 1")
        val total9 = db.getScalarLocalDateTime("select test_ldtime from db_tests where id = 1")

        // CASE 5: Execute a sql insert
        val id1 = db.insert("insert into `city`(`name`) values( 'ny' )")

        // CASE 6: Execute a sql insert using parameters
        val id2 = db.insert("insert into `city`(`name`) values( ? )", listOf("ny"))

        // CASE 7: Execute a sql update
        val count7 = db.update("update `city` set `alias` = 'nyc' where id = 2")

        // CASE 8: Execute a sql udpate using parameters
        val count8 = db.update("update `city` set `alias` = 'nyc' where id = ?", listOf(id2))

        // CASE 9: Deletes are same as updates
        val count9a = db.update("delete from `city` where id = 2")
        val count9b = db.update("delete from `city` where id = ?", listOf(2))


        // ===============================================================
        // STORED PROCS
        // ===============================================================
        // CASE 10: Call a stored proc that updates data
        val count10 = db.callUpdate("dbtests_activate_by_id", listOf(id2))

        // CASE 11: Call a stored proc that fetches data
        val count11 = db.callQuery("dbtests_max_by_id",
                callback = { rs -> rs.getString(0) }, inputs = listOf(id2))

        // ===============================================================
        // MODELS / MAPPERS
        // ===============================================================
        // CASE 12: Map a record to an model using the mapper component
        // The mapper will load a schema from the User class by checking
        // for "Field" annotations
        val userModelSchema = ModelMapper.loadSchema(User::class)
        val mapper = EntityMapper(userModelSchema)
        val item1 = db.mapOne<User>("select * from `user` where id = 1", mapper)
        println(item1)

        // CASE 13: Map multiple records
        val items = db.mapMany<User>("select * from `user` where id < 5", mapper)
        println(items)

        // CASE 14: Create the table using the model
        // Be careful with this, ensure you are using a connection string
        // with limited permissions
        createTable(db, userModelSchema)

        // CASE 15: Drop a table
        // Be careful with this, ensure you are using a connection string
        // with limited permissions.
        db.dropTable("user")
        

```

