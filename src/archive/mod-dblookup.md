---
layout: start_page_mods_utils
title: module DbLookup
permalink: /kotlin-mod-dblookup
---

# DbLookup

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Database access utilty to query and manage data using JDBC for MySql. Other database support coming later. | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.db  |
| **source core** | slatekit.common.db.DbLookup.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_DbLookup.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_DbLookup.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 



// optional 
import slatekit.common.Result
import slatekit.common.conf.ConfFuncs
import slatekit.common.db.DbCon
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.DbLookupCompanion.defaultDb
import slatekit.common.db.DbLookup.DbLookupCompanion.namedDbs
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


        // These examples just shows the database connection registration
        // There is separate Db component in slatekit.common.db.Db
        // that handles db functions: query, insert, update, delete, scalar

        // CASE 1: The DbLookup component holds all the database
        // connections associated with a name. You can also set
        // the default database. In this case, no db connections
        // have been registered, so it will be empty.
        val dbs1 = DbLookup()
        showResult(dbs1.default())


        // CASE 2: Create the DbLookup using just 1 explicit connection
        val dbs3: DbLookup = defaultDb(
                DbConString(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://localhost/default",
                        "root",
                        "abcdefghi"
                )
        )
        showResult(dbs3.default())


        // CASE 3: Create the DbLookup using just 1 connection from a file in the user directory
        // e.g. on windows: C:\Users\kv\slatekit\conf\db.txt
        //
        // NOTES:
        // 1. This is much safer and the recommended approach to storing DB connections.
        // 2. You should also encrypt the username/password
        //
        // driver:com.mysql.jdbc.Driver
        // url:jdbc:mysql://localhost/World
        // user:root
        // password:123abc
        val dbs2: DbLookup = defaultDb(ConfFuncs.readDbCon("user://.slatekit/conf/db.txt")!!)
        showResult(dbs2.default())


        // CASE 4: Register connection and link to a key "user_db" using credentials from user folder
        val dbs4: DbLookup = namedDbs(
            listOf(Pair("users", ConfFuncs.readDbCon("user://.slatekit/conf/db_default.txt")!!))
        )
        showResult(dbs4.named("users"))

        // CASE 5: Register multiple connections by api ( "users", "files" )
        val dbs5 = namedDbs(listOf(
            Pair(
                "users", DbConString(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost/users",
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
        showResult(dbs5.named("users"))
        showResult(dbs5.named("files"))
        

```

