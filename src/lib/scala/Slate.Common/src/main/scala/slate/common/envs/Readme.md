# Env

| field | value  | 
|:--|:--|
| **desc** | Environment selector and validator for environments such as (local, dev, qa, stg, prod) ) | 
| **date**| 2016-11-21T16:49:15.685 |
| **version** | 0.9.1  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.envs  |
| **source core** | slate.common.envs.Env.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/envs](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/envs)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Env.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Env.scala) |
| **depends on** |   |

## Import
```scala 
// required 
import slate.common.console.ConsoleWriter
import slate.common.envs._
import slate.common.results.ResultSupportIn
import slate.common.{Ensure, ListMap, Todo}


// optional 
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala



    // CASE 1: Build a list of environments
    val envs1 = new Envs(List[EnvItem](
      EnvItem("loc", Env.DEV , desc = "Dev environment (local)" ),
      EnvItem("dev", Env.DEV , desc = "Dev environment (shared)" ),
      EnvItem("qa1", Env.QA  , desc = "QA environment  (current release)" ),
      EnvItem("qa2", Env.QA  , desc = "QA environment  (last release)" ),
      EnvItem("stg", Env.UAT , desc = "STG environment (demo)" ),
      EnvItem("pro", Env.PROD, desc = "LIVE environment" )
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

