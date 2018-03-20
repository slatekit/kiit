---
layout: start_page_mods_utils
title: module Todo
permalink: /kotlin-mod-todo
---

# Todo

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A programmatic approach to marking and tagging code that is strongly typed and consistent | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common  |
| **source core** | slatekit.common.Todo.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Todo.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Todo.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import TODO.BUG
import TODO.IMPLEMENT
import TODO.REFACTOR
import TODO.REMOVE
import slatekit.common.DateTime
import slatekit.common.console.*



// optional 
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok


```

## Setup
```kotlin

n/a

```

## Usage
```kotlin


    // About: Strongly typed, structured representation of code notes/tasks
    // This is in code to enforce consistent usage and to be able
    // to track code usages

    // Use case 1: Implement
    IMPLEMENT("Component 1", "This code needs further error handling" )

    // Use case 2: Supply a block of code to refactor
    REFACTOR("Feature 2", "Refactor logic to handle empty values", {
      // Your code to refactor goes here
    })

    // Use case 3: Mark a bug
    BUG("Component 3", "invalid data, bug fix needed", "JIRA:12434" )

    // Use case 4: Code removal tag
    REMOVE("Story 123", "@kishore, this code no longer needed", {
      // Your code to remove here.
    })
    

```

