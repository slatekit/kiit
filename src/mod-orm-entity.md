---
layout: start_page_mods_utils
title: module Orm-Entity
permalink: /kotlin-mod-orm-entity
---

# Orm-Entity

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A base class for persistent domain entities | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.entities.jar  |
| **namespace** | slatekit.common.entities  |
| **source core** | slatekit.common.entities.Entity.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Entities.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Entities.kt){:.url-ch} |
| **depends on** |  slatekit.common.jar  |

## Import
```kotlin 
// required 
import slatekit.entities.core.*


// optional 
import slatekit.common.DateTime
import slatekit.common.Random
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


  // CASE 1 : Create a new entity class that extends Entity base class with built in support for
  // 1. id field ( primary key )
  data class EmployeeV1 (
                          override val id        : Long = 0,
                          val firstName : String = "John",
                          val lastName  : String = "Doe"
                        ) : EntityWithId


  // CASE 2 : Create a new entity class with support for
  // 1. id   ( primary key + auto inc )
  // 2. time ( created at, updated at )
  // 3. user ( created by, updated by )
  data class EmployeeV2 (
                            override val id        : Long = 0,
                            val firstName : String = "John",
                            val lastName  : String = "Doe",
                            override val createdAt : DateTime = DateTime.now(),
                            override val updatedAt : DateTime = DateTime.now(),
                            override val updatedBy : Long = 0,
                            override val createdBy : Long = 0
                        )
    : EntityWithId, EntityWithTime, EntityWithUser



  // CASE 3 : Create a new entity class with support for:
  // 1. id   ( primary key + auto inc )
  // 2. time ( created at, updated at )
  // 3. user ( created by, updated by )
  // 4. guid ( unique id              )
  data class EmployeeV3(
                         override val id        : Long = 0,
                         val firstName : String = "John",
                         val lastName  : String = "Doe",
                         override val createdAt : DateTime = DateTime.now(),
                         override val updatedAt : DateTime = DateTime.now(),
                         override val updatedBy : Long = 0,
                         override val createdBy : Long = 0,
                         override val uniqueId  : String = Random.stringGuid(false)
                       )
    : EntityWithId, EntityWithTime, EntityWithUser, EntityWithGuid
  {
    // The unique id is a guid and unique regardless of environment ( dev, qa, staging, prod )
    // It serves as a easy way to check for existing items across different environments and
    // also makes it easy to import/export items from 1 environment to another ( e.g. pro to dev )
  }


  // CASE 4 : Create a new entity class with support for:
  // 1. id   ( primary key + auto inc )
  // 2. time ( created at, updated at )
  // 3. user ( created by, updated by )
  // 4. guid ( unique id              )
  // using IEntityWithMeta trait which combines IEntityWithTime, IEntityWithUser, IEntityWithGuid
  data class EmployeeV4(
                          override val id        : Long = 0,
                          val firstName : String = "John",
                          val lastName  : String = "Doe",
                          override val createdAt : DateTime = DateTime.now(),
                          override val updatedAt : DateTime = DateTime.now(),
                          override val updatedBy : Long = 0,
                          override val createdBy : Long = 0,
                          override val uniqueId  : String = Random.stringGuid(false)
                        )
    : EntityWithId, EntityWithMeta



```

