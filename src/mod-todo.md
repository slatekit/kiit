---
layout: start_page_mods_utils
title: module Todo
permalink: /mod-todo
---

# Todo

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | A programmatic approach to marking and tagging code that is strongly typed and consistent | 
| **date**| 2017-04-12T22:59:15.335 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common  |
| **source core** | slate.common.Todo.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/common](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/common)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Todo.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Todo.scala) |
| **depends on** |   |

## Import
```scala 
// required 

import slate.common.Result
import slate.common.Todo._


// optional 
import slate.core.cmds.Cmd
import slate.common.results.ResultSupportIn


```

## Setup
```scala

n/a

```

## Usage
```scala


    // About: Strongly typed, structured representation of code notes/tasks
    // This is in code to enforce consistent usage and to be able
    // to track code usages

    // Use case 1: Implement
    implement("Component 1", "This code needs further error handling" )

    // Use case 2: Supply a block of code to refactor
    refactor("Feature 2", "Refactor logic to handle empty values", Some( () => {
      // Your code to refactor goes here
    }))

    // Use case 3: Mark a bug
    bug("Component 3", "invalid data, bug fix needed", "JIRA:12434" )

    // Use case 4: Code removal tag
    remove("Story 123", "@kishore, this code no longer needed", Some( () => {
      // Your code to remove here.
    }))
    

```

