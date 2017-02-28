---
layout: start_page_mods_utils
title: module Data
permalink: /mod-data
---

# Data

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Database access utilty to query and manage data using JDBC for MySql. Other database support coming later. | 
| **date**| 2017-02-27T17:37:20.107 |
| **version** | 1.2.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.databases  |
| **source core** | slate.common.databases.Db.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/databases](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/databases)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Database.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Database.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.databases._
import slate.common.mapper.Mapper
import slate.common.databases.DbLookup._



// optional 
import slate.common.results.ResultSupportIn
import slate.core.common.Conf
import slate.core.cmds.Cmd
import slate.entities.core.EntityMapper
import scala.reflect.runtime.universe.{typeOf}
import slate.examples.common.User


```

## Setup
```scala

n/a

```

## Usage
```scala


    // These examples show 2 parts of the database component:
    // 1. Database connection registration
    // 2. Database reading/writing

    // Manual creation via class
    val dbs1 = new DbLookup()
    showResult( dbs1.default )

    // CASE 2: Default database connection using credentials from users folder with content:
    // e.g. on windows: C:\Users\kv\slatekit\conf\db.txt
    //
    // driver:com.mysql.jdbc.Driver
    // url:jdbc:mysql://localhost/World
    // user:root
    // password:123abc
    val dbs2 = defaultDb( DbUtils.loadFromUserFolder(".slatekit", "db_default.txt"))
    showResult( dbs2.default )

    // CASE 3: Default database connection explicitly
    val dbs3 = defaultDb(
      new DbConString (
        "com.mysql.jdbc.Driver",
        "jdbc:mysql://localhost/default",
        "root",
        "abcdefghi"
      )
    )
    showResult( dbs3.default )

    // CASE 4: Register connection and link to a key "user_db" using credentials from user folder
    val dbs4 = namedDbs( ("users", DbUtils.loadFromUserFolder(".slate", "db_default.txt") ))
    showResult( dbs4.named("users") )

    // CASE 5: Register multiple connections by name ( "users", "files" )
    val dbs5 = namedDbs(
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
    showResult( dbs5.named("users") )
    showResult( dbs5.named("fiels") )

    // CASE 6: Register connection as a shard and link to a group
    val dbs6 = groupedDbs(
      (
        "usa_east", List[(String,DbConString)]
        (
          (
            "01", new DbConString (
              "com.mysql.jdbc.Driver",
              "jdbc:mysql://usa_east/01",
              "root",
              "abcdefghi"
          )),
          (
            "02", new DbConString (
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://usa_east/02",
            "root",
            "abcdefghi"
          ))
        )
      ),
      (
        "usa_west", List[(String,DbConString)]
        (
          (
            "01", new DbConString (
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://usa_west/01",
            "root",
            "abcdefghi"
          )),
          (
            "02", new DbConString (
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://usa_west/02",
            "root",
            "abcdefghi"
          ))
        )
      )
    )
    showResult( dbs6.group( "usa_east", "01") )
    showResult( dbs6.group( "usa_east", "02") )
    showResult( dbs6.group( "usa_west", "01") )
    showResult( dbs6.group( "usa_west", "02") )


    // CASE 7: Now create the database class and start using some basic methods
    val db = new Db(new Conf().dbCon("db").get)

    // CASE 8: Open the database
    db.open()

    // CASE 8: Get scalar int value
    val total1 = db.getScalarInt("select count(*) from City")

    // CASE 9: Execute a sql insert
    val id1 = db.insert("insert into `city`(`name`) values( 'ny' )" )

    // CASE 10: Execute a sql insert using
    val id2 = db.insert("insert into `city`(`name`) values( 'ny' )" )

    // CASE 10: Execute a sql update
    val total2 = db.update("update `city` set `name` = 'ny' where id = 2")

    // CASE 11: Map a record to an model using the mapper component
    val model = Mapper.loadSchema(typeOf[User])
    val mapper = new EntityMapper(model)
    val item1 = db.mapOne("select * from `city` where id = 1", mapper)
    println( item1 )

    // CASE 12: Map multiple records
    val items = db.mapMany("select * from `city` where id < 5", mapper)
    println( items )

    

```

