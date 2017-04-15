---
layout: start_page_mods_utils
title: module Folders
permalink: /mod-folders
---

# Folders

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Standardized application folder setup; includes conf, cache, inputs, logs, outputs | 
| **date**| 2017-04-12T22:59:14.646 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.info  |
| **source core** | slate.common.info.Folders.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/info](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/info)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Folders.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Folders.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.Result
import slate.common.app.LocationUserDir
import slate.common.info.Folders


// optional 
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala



    // CASE 1: Build a folder structure for an application
    // Explicitly indicate user-directory with the following folder structure
    // NOTE: Slate Kit based applications are setup this way.
    //
    // /c/users/{user}/
    //  - company-1
    //    - department-1
    //      - app-1
    //        - conf
    //        - cache
    //        - inputs
    //        - logs
    //        - outputs
    val folders = Folders(
      location = LocationUserDir,
      home     = System.getProperty("user.dir"),
      root     = Some("company-1"),
      group    = Some("department-1"),
      app      = "app-1",
      cache    = "cache",
      inputs   = "input",
      logs     = "logs",
      outputs  = "output",
      conf     = "conf"
    )

    // CASE 2: Build a folder structure for an application
    // Same as Case 1 above but using a short-hand approach
    val folders2 = Folders.userDir(
      root     = "company-1",
      group    = "department-1",
      app      = "app-1"
    )

    // CASE 3: Create the folders if they do not exist
    folders.create()

    // CASE 4: Get the individual paths to the folders
    println("app    :" + folders.pathToApp())
    println("cache  :" + folders.pathToCache)
    println("conf   :" + folders.pathToConf)
    println("input  :" + folders.pathToInputs)
    println("logs   :" + folders.pathToLogs)
    println("output :" + folders.pathToOutputs)

    

```

