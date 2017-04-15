---
layout: start_page_mods_utils
title: module Env
permalink: /mod-env
---

# Env

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Environment selector and validator for environments such as (local, dev, qa, stg, prod) ) | 
| **date**| 2017-04-12T22:59:14.581 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.envs  |
| **source core** | slate.common.envs.Env.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/envs](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/envs)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Env.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Env.scala) |
| **depends on** |   |

## Import
```scala 
// required 

import slate.common.Result
import slate.common.envs._


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



    // CASE 1: Build a list of environments
    val envs1 = new Envs(List[Env](
      Env("loc", Dev , desc = "Dev environment (local)" ),
      Env("dev", Dev , desc = "Dev environment (shared)" ),
      Env("qa1", Qa  , desc = "QA environment  (current release)" ),
      Env("qa2", Qa  , desc = "QA environment  (last release)" ),
      Env("stg", Uat , desc = "STG environment (demo)" ),
      Env("pro", Prod, desc = "LIVE environment" )
    ))

    // CASE 2: Use the default list of environments ( same as above )
    val envs = Env.defaults()

    // CASE 3: Get one of the environments by name
    val qa1 = envs("qa1")
    println( qa1 )

    // CASE 4: Validate one of the environments by name
    println( envs.isValid("qa2") )

    // CASE 5: Current environment ( nothing - none selected )
    println( envs.current.isDefined )

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

