---
layout: start_page
title: module Orm-Model
permalink: /mod-orm-model
---

# Orm-Model

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A model schema builder | 
| **date**| 2017-04-12T22:59:15.461 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common  |
| **source core** | slate.common.Model.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/common](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/common)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Model.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Model.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 
import slate.common.{Result, DateTime, ModelField, Model}
import scala.reflect.runtime.universe._


// optional 
import slate.common.info.Host
import slate.common.databases.DbBuilder
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd
import slate.examples.common.User


```

## Setup
```scala

n/a

```

## Usage
```scala


    // ABOUT:
    // The Model component allows you to easily build up a model with fields
    // This allows you to have a schema / structure representing a data model.
    // With the structure in place, it helps facilitate code-generation.
    // Also, the ORM / Mapper of Slate Kit internally builds a model for each
    // Scala class that is mapped by the ORM.

    // CASE 1: specify the name of the model e.g. "Resource"
    // NOTE: The model is IMMUTABLE ( any additions of fields will result in a new model )
    var model = new Model("Resource", "slate.ext.resources.Resource")

    // CASE 2. add a field for uniqueness / identity
    model = model.addId  (name = "id", autoIncrement = true, dataType = typeOf[Long])

    // CASE 3: add fields for text, bool, int, date etc.
    model = model.addText  (name = "key"     , isRequired = true, maxLength = 30)
                 .addText  (name = "name"       , isRequired = true, maxLength = 30)
                 .addText  (name = "category"   , isRequired = true, maxLength = 30)
                 .addText  (name = "country"    , isRequired = true, maxLength = 30)
                 .addText  (name = "region"     , isRequired = true, maxLength = 30)
                 .addText  (name = "aggRegion"  , isRequired = true, maxLength = 30)
                 .addText  (name = "aggCategory", isRequired = true, maxLength = 30)
                 .addText  (name = "links"      , isRequired = true, maxLength = 30)
                 .addText  (name = "owner"      , isRequired = true, maxLength = 30)
                 .addText  (name = "status"     , isRequired = true, maxLength = 30)
                 .addInt   (name = "recordState", isRequired = true)
                 .addObject(name = "hostInfo"   , isRequired = true, dataType = typeOf[Host])
                 .addDate  (name = "created"    , isRequired = true                )
                 .addDate  (name = "updated"    , isRequired = true                )

    // CASE 3: add fields for text, bool, int, date etc.
    model = new Model("Resource", "", dataType = Some(typeOf[User]), desc = "", tableName = "users", _propList = Some(List[ModelField](
                 new ModelField(name = "key"        , isRequired = true, maxLength = 30),
                 new ModelField(name = "name"       , isRequired = true, maxLength = 30),
                 new ModelField(name = "recordState", isRequired = true, dataType = typeOf[Int]),
                 new ModelField(name = "hostInfo"   , isRequired = true, dataType = typeOf[Host]),
                 new ModelField(name = "created"    , isRequired = true, dataType = typeOf[DateTime]),
                 new ModelField(name = "updated"    , isRequired = true, dataType = typeOf[DateTime])
      )))

    // CASE 4. check for any fields
    showResult( "any fields: " + model.any )

    // CASE 5. total number of fields
    showResult( "total fields: " + model.size )

    // CASE 6. get the id field
    showResult( "id field: " + model.hasId )

    // CASE 7. get the id field
    showResult( "id field: " + model.idField )

    // CASE 8. string representation of field
    showResult( "id field to string: " + model.idField.toString)

    // CASE 8. access the fields
    showResult ("access to fields : " + model.fields.size)

    // CASE 9. get name + full name of model
    showResult ("model name/fullName: " + model.name + ", " + model.fullName)

    // CASE 10. build up the table sql for this model
    showResult( "table sql : " + new DbBuilder().addTable(model))
    

```

