---
layout: start_page
title: module Orm-Mapper
permalink: /mod-orm-mapper
---

# Orm-Mapper

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A mapper that converts a entity to a sql create/updates | 
| **date**| 2017-04-12T22:59:15.482 |
| **version** | 1.4.0  |
| **jar** | slate.entities.jar  |
| **namespace** | slate.common.entities  |
| **source core** | slate.common.entities.EntityMapper.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/entities](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/entities)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Mapper.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Mapper.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.mapper.Mapper
import scala.annotation.meta.field
import scala.reflect.runtime.universe._
import slate.entities.core._
import slate.common.{Result, Field, DateTime, Reflector}
import slate.common.databases.DbBuilder


// optional 
import slate.core.cmds.Cmd
import slate.common.results.ResultSupportIn


```

## Setup
```scala


  case class Consultant (

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

                        ) extends EntityWithId with EntityUpdatable[Consultant]
  {
  }
  

```

## Usage
```scala


    // CASE 1: Load the mapper with schema from the annotations on the model
    val model = Mapper.loadSchema(typeOf[Consultant])
    val mapper = new EntityMapper(model)

    // CASE 2: Create instance for testing
    val person = new Consultant(
     firstName = "share",
     lastName = "job",
     lastLogin = DateTime.now(),
     email = "john.doe@gmail.com",
     isEmailVerified = false,
     status = 0,
     createdAt = DateTime.now(),
     updatedAt = DateTime.now()
    )

    // CASE 4: Get the sql for create
    val sqlCreate = mapper.mapToSql(person, update = false, fullSql = true)
    println(sqlCreate)

    // CASE 5: Get the sql for update
    val sqlForUpdate = mapper.mapToSql(person, update = true, fullSql = true)
    println(sqlForUpdate)

    // CASE 6: Generate the table schema for mysql from the model
    println( "table sql : " + new DbBuilder().addTable(model))
    

```

