---
layout: start_page_mods_utils
title: module Serialization
permalink: /kotlin-mod-serialization
---

# Serialization

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Serializers for data classes to generate CSV, Props, HOCON, JSON files | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.serialization  |
| **source core** | slatekit.common.serialization.Serializer.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Serialization.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Serialization.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.Result
import slatekit.common.serialization.*


// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.results.ResultFuncs.ok
import slatekit.examples.common.User



```

## Setup
```kotlin


    // Setup some sample data to serialize
    val user1 = User(2, "superman@metro.com", "super", "man", true, 35)
    val user2 = User(3, "batman@gotham.com" , "bat"  , "man", true, 35)
    val users = listOf(user1, user2)
    

```

## Usage
```kotlin


    // The serializers come in very handy during the serialization of
    // entities in the ORM and is used in the CLI and HTTP server.
    // However, they are general purpose and can be used else where.
    // They are :
    // 1. Optimized for data classes
    // 2. Use reflection to get the properties to serialize
    // 3. Support recursion into nested objects
    // 4. Handle lists of type List<*> and maps of basic types Map<*,*>

    // Case 1: Serialize CSV
    val csvSerializer = SerializerCsv()
    val csvData = csvSerializer.serialize(users)
    println("CSV ====================")
    println(csvData)


    // Case 2: Serialize Properties files
    val propsSerializer = SerializerProps()
    val propsData = propsSerializer.serialize(users)
    println("HCON ====================")
    println(propsData)


    // Case 3: Serialize JSON
    val jsonSerializer = SerializerJson()
    val jsonData = jsonSerializer.serialize(users)
    println("JSON ====================")
    println(jsonData)

    

```


## Output


```bat
2, "superman@metro.com", "super", "man", true, 35
3, "batman@gotham.com", "bat", "man", true, 35
```

```bat
id = 2
email = superman@metro.com
firstName = super
lastName = man
isMale = true
age = 35


id = 3
email = batman@gotham.com
firstName = bat
lastName = man
isMale = true
age = 35
```

```bat
[
  {"id" : 2, "email" : "superman@metro.com", "firstName" : "super", "lastName" : "man", "isMale" : true, "age" : 35},
  {"id" : 3, "email" : "batman@gotham.com", "firstName" : "bat", "lastName" : "man", "isMale" : true, "age" : 35}
]
```
