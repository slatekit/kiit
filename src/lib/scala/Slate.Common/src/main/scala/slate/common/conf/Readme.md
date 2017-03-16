---
layout: start_page_mods_utils
title: module Config
permalink: /mod-config
---

# Config

{: .table .table-striped .table-bordered}
|:--|:--|
| **desc** | Thin wrapper over typesafe config with decryption support, uri loading, and mapping of database connections and api keys | 
| **date**| 2017-03-12T23:33:48.914 |
| **version** | 1.4.0  |
| **jar** | slate.common.jar  |
| **namespace** | slate.common.conf  |
| **source core** | slate.common.conf.Config.scala  |
| **source folder** | [/src/lib/scala/Slate.Common/src/main/scala/slate/common/conf](https://github.com/code-helix/slatekit/tree/master/src/lib/scala/Slate.Common/src/main/scala/slate/common/conf)  |
| **example** | [/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Config.scala](https://github.com/code-helix/slatekit/tree/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Config.scala) |
| **depends on** |   |

## Import
```scala 
// required 

import slate.common.Result
import slate.core.common.Conf


// optional 
import slate.common.databases.{DbCon, DbConString}
import slate.common.encrypt.Encryptor
import slate.common.envs.{Dev, Env}
import slate.common.results.ResultSupportIn
import slate.core.cmds.Cmd


```

## Setup
```scala

n/a

```

## Usage
```scala


    // CASE 1: Load up config from application.conf in resources
    val conf = new Conf()
    println( "env.name: " + conf.getString("env.name") )
    println( "env.region: " + conf.getStringOrElse("env.region", "usa") )
    println( "db.enabled: " + conf.getBool("db.enabled") )
    println()


    // CASE 2: Get the environment selection ( env, dev, qa ) from conf or default
    val env = conf.env().getOrElse(new Env("local", Dev))
    println( s"${env.name}, ${env.mode.name}, ${env.key}")
    println()


    // CASE 3: Inherit config from another config in resources folder
    // e.g. env.dev.conf ( dev environment ) can inherit from env.conf ( common )
    val confs1 = Conf.loadWithFallback("env.dev.conf", "env.conf")
    val dbConInherited = confs1.dbCon()
    printDbCon ( "db con - inherited : ", dbConInherited )


    // CASE 4: Override inherited config settings
    // e.g. env.loc.conf ( local environment ) overrides settings inherited from env.conf
    val confs2 = Conf.loadWithFallback("env.loc.conf", "env.conf")
    val dbConOverride = confs2.dbCon()
    printDbCon ( "db con - override : ", dbConOverride )


    // CASE 5: Multiple db settings, get 1 using a prefix
    // e.g. env.qa.conf ( qa environment ) with 2 db settings get one with "qa2" prefix.
    val confs3 = Conf.loadWithFallback("env.qa1.conf", "env.conf")
    val dbConMulti = confs3.dbCon("qa1")
    printDbCon ( "db con - multiple : ", dbConMulti )


    // CASE 6: File from user directory:
    // You can refer to a file path using a uri syntax:
    //
    // SYNTAX:
    // - "jars://"  refer to resources directory in the jar.
    // - "user://"  refer to user.home directory.
    // - "file://"  refer to an explicit path to the file
    // - "file://"  refer to a relative path to the file from working directory

    // EXAMPLES:
    // - jar://env.qa.conf
    // - user://slatekit/conf/env.qa.conf
    // - file://c:/slatekit/system/slate.shell/conf/env.qa.conf
    // - file://./conf/env.qa.conf
    //
    // CONFIG
    //
    // db {
    //   location: "user://slatekit/conf/db.conf"
    // }
    val confs4 = Conf.loadWithFallback("env.pro.conf", "env.conf")
    val dbConFile = confs4.dbCon( prefix = "db")
    printDbCon ( "db con - file ref: ", dbConFile )


    // CASE 7: Decryp encrypted strings in the config file
    // e.g.
    // db.user = "@{decrypt('8r4AbhQyvlzSeWnKsamowA')}"
    val encryptor = new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")
    val confs5 = Conf.loadWithFallback("env.qa1.conf", "env.conf", enc = Some(encryptor) )
    println ( "db user decrypted : " + confs5.getString("db.user") )
    println ( "db pswd decrypted : " + confs5.getString("db.pswd") )
    println()
    

```


## Output

```bat
 env.name: lc1
  db.enabled: true

  lc1, dev, dev : lc1


  db con - inherited :
  driver: com.mysql.jdbc.Driver
  url   : jdbc:mysql://localhost/db1
  user  : root
  pswd  : 123456789


  db con - override :
  driver: com.mysql.jdbc.Driver
  url   : jdbc:mysql://localhost/db1
  user  : root
  pswd  : 123456789


  db con - multiple :
  driver: com.mysql.jdbc.Driver
  url   : jdbc:mysql://localhost/db1
  user  : root
  pswd  : 123456789


  db con - file ref:
  driver: com.mysql.jdbc.Driver
  url   : jdbc:mysql://localhost/test1
  user  : root
  pswd  : t$123456789


  db user decrypted : root
  db pswd decrypted : 123456789
```
  