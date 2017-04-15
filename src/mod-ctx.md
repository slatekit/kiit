---
layout: start_page_mods_infra
title: module Ctx
permalink: /mod-ctx
---

# Ctx

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | An application context to contain common dependencies such as configs, logger, encryptor, etc, to be accessible to other components | 
| **date**| 2017-04-12T22:59:15.678 |
| **version** | 1.4.0  |
| **jar** | slate.core.jar  |
| **namespace** | slate.core.common  |
| **source core** | slate.core.common.AppContext.scala  |
| **source folder** | [/src/lib/scala/Slate.Core/src/main/scala/slate/core/common](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Core/src/main/scala/slate/core/common)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Context.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Context.scala) |
| **depends on** |  slate.common.jar  |

## Import
```scala 
// required 

import slate.common.Result
import slate.common.logging.LoggerConsole
import slate.common.args.{ArgsSchema, Args}
import slate.common.envs.{Dev, Env}
import slate.common.info.{About, Lang, Host}
import slate.core.app.{AppRunner}
import slate.core.common.{Conf, AppContext}
import slate.entities.core.Entities


// optional 
import slate.common.results.{ResultCode, ResultSupportIn}
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala



    // OVERVIEW:
    // The AppContext is a container for common dependencies
    // across different components in an application. It contains
    // basic info about the app and the following :
    // 1.  args: parsed command line arguments
    // 2.  env : the selected environment ( dev, qa, uat, prod )
    // 3.  conf: the config object
    // 4.  log : the logger
    // 5.  inf : info about the application ( app name, etc )
    // 6.  ent : the entities which are mapped ORM entities ( can be empty )
    // 7.  host: the computer host running the app
    // 8.  lang: the version info of java/scala running the app
    // 9.  dbs : a list of available database connections by name
    // 10. dirs: the folders objects outlining core directories for the app
    // 11. enc : the encryption service to handle encryption/decryption
    //
    // NOTE: Many of these are OPTIONAL and can be set to None

    // CASE 1: Build info about the app ( to be reused for the examples below )
    val info = About(
      id       = "sample-app-1",
      name     = "Sample App-1",
      desc     = "Sample application 1",
      company  = "Company 1",
      group    = "Department 1",
      region   = "New York",
      url      = "http://company1.com/dep1/sampleapp-1",
      contact  = "dept1@company1.com",
      version  = "1.0.1",
      tags     = "sample app slatekit scala",
      examples = ""
    )

    // CASE 2: Build a simple context with minimal info that includes:
    // - default arguments ( command line )
    // - dev environment
    // - new Conf() representing conf from "application.conf"
    // - default logger ( console )
    // - entities ( registrations for orm )
    val ctx1 = AppContext(
      arg   = Args()                                  ,
      env   = Env("dev", Dev, "ny", "dev environment"),
      cfg   = new Conf()    ,
      log   = new LoggerConsole(),
      ent   = new Entities(),
      inf   = info,
      host  = Host.local()  ,
      lang  = Lang.asScala()
    )

    // CASE 3: Typically your application will want to derive the
    // context from either the command line args and or the config
    // There is a builder method takes command line arguments and
    // other inputs and constructs the context. This example shows
    // only providing the arguments to build the context

    // CASE 3A: This checks for "-env" arg and loads the corresponding
    // inherited config environment (refer to config in utils for more info )
    // but basically, this loads the env.dev.conf with fallback to env.conf
    // 1. "env.dev.conf" ( environment specific )
    // 2. "env.conf"     ( common / base line   )
    val ctx2 = AppRunner.build(Some(Array[String]("-env=dev -log -log.level=debug")))
    showContext(ctx2)

    // CASE 3B: This example shows providing the args schema for parsing the args
    // refer to Args in utils for more info.
    // NOTE: There are additional parameters on the build function ( callbacks )
    // to allow you to get the context and modify it before it is returned.
    val schema = new ArgsSchema()
      .text("env"        , "the environment to run in"      , false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
      .text("region"     , "the region linked to app"       , false, "us"   , "us"   , "us|europe|india|*")
      .text("config.loc" , "location of config files"       , false, "jar"  , "jar"  , "jar|conf")
      .text("log.level"  , "the log level for logging"      , false, "info" , "info" , "debug|info|warn|error")
    val ctx3 = AppRunner.build(Some(Array[String]("-env=dev -log -log.level=debug")), Some(schema))
    showContext(ctx3)

    // CASE 3C: You can also access an error context representing an invalid context
    val ctx4 = AppContext.err(ResultCode.BAD_REQUEST, Some("Bad context, invalid inputs supplied"))
    showContext(Some(ctx4))

    

```

