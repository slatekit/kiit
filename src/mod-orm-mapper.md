---
layout: start_page_mods_utils
title: module Orm-Mapper
permalink: /kotlin-mod-orm-mapper
---

# Orm-Mapper

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A mapper that converts a entity to a sql create/updates | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.entities.jar  |
| **namespace** | slatekit.common.entities  |
| **source core** | slatekit.common.entities.EntityMapper.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Mapper.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Mapper.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Result
import slatekit.common.db.types.DbSourceMySql
import slatekit.common.Mapper
import slatekit.meta.models.Model
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd
import slatekit.entities.core.EntityMapper
import slatekit.entities.core.EntityWithId
import slatekit.meta.buildAddTable
import slatekit.meta.models.ModelMapper



// optional 


```

## Setup
```kotlin


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

## Usage
```kotlin


        // NOTE: There are 3 different ways to load the schema of the entity.
        // 1. automatically using annotations
        // 2. manually using properties references
        // 3. manually using methods and string names


        // CASE 1: Load the schema from the annotations on the model
        val schema1 = ModelMapper.loadSchema(Movie::class)


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
        val schema3 = Model(Movie::class)
                .addId(Movie::id, true)
                .addText    ("title"     , "Title of movie"         , true, 1, 30)
                .addText    ("category"  , "Category (action|drama)", true, 1, 20)
                .addBool    ("playing"   , "Whether its playing now")
                .addDouble  ("rating"    , "Rating from users"      )
                .addDateTime("released"  , "Date of release"        )
                .addDateTime("createdAt" , "Who created record"     )
                .addLong    ("createdBy" , "When record was created")
                .addDateTime("updatedAt" , "Who updated record"     )
                .addLong    ("updatedBy" , "When record was updated")


        // CASE 4: Now with a schema of the entity, you create a mapper
        val mapper = EntityMapper (schema1)

        // Create sample instance to demo the mapper
        val movie = Movie(
                        title = "Man Of Steel",
                        category = "action",
                        playing = false,
                        cost = 100,
                        rating = 4.0,
                        released = DateTime.of(2015, 7, 4)
                )

        // CASE 5: Get the sql for create
        val sqlCreate = mapper.mapToSql(movie, update = false, fullSql = true)
        println(sqlCreate)

        // CASE 6: Get the sql for update
        val sqlForUpdate = mapper.mapToSql(movie, update = true, fullSql = true)
        println(sqlForUpdate)

        // CASE 7: Generate the table schema for mysql from the model
        println("table sql : " + buildAddTable(DbSourceMySql(), schema1))
        

```

