---
layout: start_page_mods_utils
title: module Model
permalink: /kotlin-mod-model
---

# Model

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Allows construction of model schema with fields for code-generation. Also used in the ORM mapper | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common  |
| **source core** | slatekit.common.Model.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Model.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Model.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.meta.models.*


// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.DateTime
import slatekit.common.auth.User
import slatekit.common.db.types.DbSourceMySql
import slatekit.common.info.Host
import slatekit.common.results.ResultFuncs.ok
import slatekit.meta.buildAddTable



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


    // ABOUT:
    // The Model component allows you to easily build up a model with fields
    // This allows you to have a schema / structure representing a data model.
    // With the structure in place, it helps facilitate code-generation.
    // Also, the ORM / Mapper of Slate Kit internally builds a model for each
    // Scala class that is mapped by the ORM.

    // CASE 1: specify the api of the model e.g. "Resource"
    // NOTE: The model is IMMUTABLE ( any additions of fields will result in a new model )
    var model = Model("Resource", "slate.ext.resources.Resource")

    // CASE 2. add a field for uniqueness / identity
    model = model.addId  (name = "id", autoIncrement = true, dataType = Long::class)

    // CASE 3: add fields for text, bool, int, date etc.
    model = model.addText(name = "key"        , isRequired = true, maxLength = 30)
                 .addText(name = "api"        , isRequired = true, maxLength = 30)
                 .addText(name = "category"   , isRequired = true, maxLength = 30)
                 .addText(name = "country"    , isRequired = true, maxLength = 30)
                 .addText(name = "region"     , isRequired = true, maxLength = 30)
                 .addText(name = "aggRegion"  , isRequired = true, maxLength = 30)
                 .addText(name = "aggCategory", isRequired = true, maxLength = 30)
                 .addText(name = "links"      , isRequired = true, maxLength = 30)
                 .addText(name = "owner"      , isRequired = true, maxLength = 30)
                 .addText(name = "status"     , isRequired = true, maxLength = 30)
                 .addInt (name = "recordState", isRequired = true)
                 .addObject     (name = "hostInfo"   , isRequired = true, dataType = Host::class)
                 .addLocalDate  (name = "activated"  , isRequired = true                )
                 .addLocalTime  (name = "checkTime"  , isRequired = true                )
                 .addLocalDateTime(name = "created"  , isRequired = true                )
                 .addDateTime(name = "updated"  , isRequired = true                )

    // CASE 3: add fields for text, bool, int, date etc.
    model = Model("Resource", "", dataType = User::class, desc = "", tableName = "users", _propList = listOf(
                 ModelField(name = "key"        , isRequired = true, maxLength = 30, dataType = String::class),
                 ModelField(name = "api"       , isRequired = true, maxLength = 30, dataType = String::class),
                 ModelField(name = "recordState", isRequired = true, dataType = Int::class),
                 ModelField(name = "hostInfo"   , isRequired = true, dataType = Host::class),
                 ModelField(name = "created"    , isRequired = true, dataType = DateTime::class),
                 ModelField(name = "updated"    , isRequired = true, dataType = DateTime::class)
      ))

    // CASE 4. check for any fields
    showResult( "any fields: " + model.any )

    // CASE 5. total number of fields
    showResult( "total fields: " + model.size )

    // CASE 6. get the id field
    showResult( "id field: " + model.hasId )

    // CASE 7. get the id field
    showResult( "id field: " + model.idField )

    // CASE 8. string representation of field
    showResult( "id field to string: " + model.idField.toString())

    // CASE 8. access the fields
    showResult ("access to fields : " + model.fields.size)

    // CASE 9. get api + full api of model
    showResult ("model api/fullName: " + model.name + ", " + model.fullName)

    // CASE 10. build up the table sql for this model
    showResult( "table sql : " + buildAddTable(DbSourceMySql(), model))
    

```

