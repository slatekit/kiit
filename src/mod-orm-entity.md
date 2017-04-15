---
layout: start_page
title: module Orm-Entity
permalink: /mod-orm-entity
---

# Orm-Entity

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A base class for persistent domain entities | 
| **date**| 2017-04-12T22:59:15.465 |
| **version** | 1.4.0  |
| **jar** | slate.entities.jar  |
| **namespace** | slate.common.entities  |
| **source core** | slate.common.entities.Entity.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/entities](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/entities)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Entities.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Entities.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.results.ResultSupportIn
import slate.entities.core._
import slate.common.{Result, Random, DateTime}


// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


  // CASE 1 : Create a new entity class that extends Entity base class with built in support for
  // 1. id field ( primary key )
  case class EmployeeV1 (
                          id        : Long = 0,
                          firstName : String = "John",
                          lastName  : String = "Doe"
                        )
    extends EntityWithId
  {
  }



  // CASE 2 : Create a new entity class with support for
  // 1. id   ( primary key + auto inc )
  // 2. time ( created at, updated at )
  // 3. user ( created by, updated by )
  case class EmployeeV2 (
                          id        : Long = 0,
                          firstName : String = "John",
                          lastName  : String = "Doe",
                          createdAt : DateTime = DateTime.now(),
                          updatedAt : DateTime = DateTime.now(),
                          updatedBy : Long = 0,
                          createdBy : Long = 0
                        )
    extends EntityWithId
      with EntityWithTime
      with EntityWithUser
  {
  }


  // CASE 3 : Create a new entity class with support for:
  // 1. id   ( primary key + auto inc )
  // 2. time ( created at, updated at )
  // 3. user ( created by, updated by )
  // 4. guid ( unique id              )
  case class EmployeeV3(
                         id        : Long = 0,
                         firstName : String = "John",
                         lastName  : String = "Doe",
                         createdAt : DateTime = DateTime.now(),
                         updatedAt : DateTime = DateTime.now(),
                         updatedBy : Long = 0,
                         createdBy : Long = 0,
                         uniqueId  : String = Random.stringGuid(false)
                       )
    extends EntityWithId
      with EntityWithTime
      with EntityWithUser
      with IEntityUnique
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
  case class EmployeeV4(
                          id        : Long = 0,
                          firstName : String = "John",
                          lastName  : String = "Doe",
                          createdAt : DateTime = DateTime.now(),
                          updatedAt : DateTime = DateTime.now(),
                          updatedBy : Long = 0,
                          createdBy : Long = 0,
                          uniqueId  : String = Random.stringGuid(false)
                        )
    extends EntityWithId
      with EntityWithMeta
  {
  }
  

```

