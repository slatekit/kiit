---
layout: start_page_mods_utils
title: module Env
permalink: /kotlin-mod-env
---

# Env

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Environment selector and validator for environments such as (local, dev, qa, stg, prod) ) | 
| **date**| 2018-03-18 |
| **version** | 0.9.9  |
| **jar** | slatekit.common.jar  |
| **namespace** | slatekit.common.envs  |
| **source core** | slatekit.common.envs.Env.kt  |
| **source folder** | [src/lib/kotlin/slatekit/](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit/){:.url-ch}  |
| **example** | [/src/apps/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Env.kt](https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Env.kt){:.url-ch} |
| **depends on** |   |

## Import
```kotlin 
// required 
import slatekit.common.envs.*



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



    // CASE 1: Build a list of environments
    val envs1 = Envs(listOf(
            Env("loc", Dev , desc = "Dev environment (local)"),
            Env("dev", Dev , desc = "Dev environment (shared)"),
            Env("qa1", Qa  , desc = "QA environment  (current release)"),
            Env("qa2", Qa  , desc = "QA environment  (last release)"),
            Env("stg", Uat , desc = "STG environment (demo)"),
            Env("pro", Prod, desc = "LIVE environment")
    ))

    // CASE 2: Use the default list of environments ( same as above )
    val envs = slatekit.common.envs.Env.defaults()

    // CASE 3: Get one of the environments by api
    val qa1 = envs.get("qa1")
    println( qa1 )

    // CASE 4: Validate one of the environments by api
    println( envs.isValid("qa2") )

    // CASE 5: Current environment ( nothing - none selected )
    println( envs.current )

    // CASE 6: Select an environment
    val envs2 = envs.select("dev")
    println( envs2 )

    // CASE 7: Get info about currently selected environment
    println( envs2.name    )
    println( envs2.isDev   )
    println( envs2.isQa    )
    println( envs2.isUat   )
    println( envs2.isProd  )
    println( envs2.current )
    

```

