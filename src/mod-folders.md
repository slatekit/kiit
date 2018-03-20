---
layout: start_page_mods_utils
title: module Folders
permalink: /kotlin-mod-folders
---

# Folders

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Standardized application folder setup; includes conf, cache, inputs, logs, outputs | 
| **date**| 2018-03-19 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.info  |
| **source core** | slatekit.common.info.Folders.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Folders.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Folders.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 



// optional 
import slatekit.common.Result
import slatekit.common.app.LocationUserDir
import slatekit.common.info.Folders
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd



```

## Setup
```kotlin

n/a

```

## Usage
```kotlin



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
        //        - temp
        val folders = Folders(
                location = LocationUserDir,
                home = System.getProperty("user.dir"),
                root = "company-1",
                group = "department-1",
                app = "app-1",
                cache = "cache",
                inputs = "input",
                logs = "logs",
                outputs = "output",
                temp = "temp",
                conf = "conf"
        )

        // CASE 2: Build a folder structure for an application
        // Same as Case 1 above but using a short-hand approach
        val folders2 = Folders.userDir(
                root = "company-1",
                group = "department-1",
                app = "app-1"
        )

        // CASE 3: Create the folders if they do not exist
        folders.create()

        // CASE 4: Get the individual paths to the folders
        println("app    :" + folders.pathToApp)
        println("cache  :" + folders.pathToCache)
        println("conf   :" + folders.pathToConf)
        println("input  :" + folders.pathToInputs)
        println("logs   :" + folders.pathToLogs)
        println("output :" + folders.pathToOutputs)
        println("output :" + folders.pathToTemp)

        

```

