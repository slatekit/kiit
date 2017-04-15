---
layout: start_page_mods_infra
title: module Utils
permalink: /mod-server
---

# Utils

|:--|:--|
| **desc** | Various utilities available in the Slate library | 
| **date**| 2016-6-29 9:13:21 |
| **version** | 0.9.1  |
| **namespace** | slate.common  |
| **source** | slate.common.Todos  |
| **example** | [Example_Utils](https://github.com/code-helix/slatekit/blob/master/src/apps/scala/slate-examples/src/main/scala/slate/examples/Example_Utils.scala) |

## Import
```scala 
// required 
coming soon

// optional 
coming soon

```

## Setup
```scala
  // SETUP 1: Initialize with just the port
  val server1 = new SlateServer( 5000 )
  
  // SETUP 2: Initialize with all the application context which contains
  // typical info needed for any application:
  // 1. env : dev, qa, staging, prod
  // 2. cfg : config values
  // 3. log : logger
  // 4. enc : encrypt/decrypt
  // 5. info: info about the app, including contact information
  //
  // NOTES: You can provide your own implementation of the Config, Logger, Encryptor
  // For a full example, refer to the sample app.
  val ctx = new AppContext (
    env  = new EnvItem( "ny.dev.01", Env.DEV, "DEV:ny.dev.01" ),
    cfg  = new Config ( Map[String,String]("env" -> "dev") ),
    log  = new LoggerConsole(),
    enc  = new Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
    inf  = new About("Server1", "example", "mobile", "ny", "http://myapp.com", "kishore@abc.com")
  )
  val server = new SlateServer(port = 5000, ctx = ctx )
  
  // Setup 3: Now register your apis( regular old scala classes )
  server.apis.register[UserApi](new UserApi(), declaredOnly = false)
  
  // An http web call like :
  // http://domain.com/api/app/users/invite
  // {
  //    email='kishore@abc.com'
  //    phone='123456789'
  //    promoCode=abc123
  // }
  //
  // will get converted to a ApiCmd. Simulate that here for testing.
  //
  // NOTE: The data in http headers/form is not copied,
  // the Akka-Http data is wrapped around another slate object
  val args = Args.parse("app.users.invite -email='kishore@abc.com' -phone='123456789' -promoCode=abc123").data.asInstanceOf[Args]
  val cmd = ApiCmd("app.users.invite", args, Some(args), None)
  server.apis.callCommand(cmd)
```

## Usage
```scala

coming soon

```

