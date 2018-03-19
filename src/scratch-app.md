---
layout: start_page
title: module Utils
permalink: /scratch-app
---

# Example 

```kotlin
  // Step 1: Extend your application from AppProcess which
  // provides life-cycle methods, and much of the boiler-plate code.
  //
  // NOTE: Ensure your environment files are setup:
  // Refer to sample app for more info.
  // - resources/env.conf
  // - resources/env.local.conf
  // - resources/env.dev.conf
  //
  // CONTEXT:
  // The AppProcess must have an AppContext ( see docs online and example below for more info)
  // which is a container to store core dependencies such as
  // - selected environment
  // - config settings
  // - logger
  // - encryptor
  // - info about app
  //
  // There are different ways you can build up the context
  // 1. Manually      ( explictly supply the components - see below )
  // 2. Automatically ( using helper functions to that check command line args )
  class SimpleApp(ctx: AppContext) : AppProcess(ctx) {

      /**
       * Life-cycle init hook: for your app to perform any initialization
       */
      override fun onInit(): Unit {
          println("app initialized")
      }
  
  
      /**
       * Life-cycle execution hook: for your app to perform the main logic
       *
       * @return
       */
      override fun onExecute(): Result<Any> {
  
          // Get access to context which has everything ( e.g env name )
          println(ctx.env.name)
  
          // Execute your work here.
          info("app executing now")
  
          return ok()
      }
  
  
      /**
       * Life-cycle end hook: called when app is shutting down
       */
      override fun onEnd(): Unit {
          info("app shutting down")
      }
   
  
      /**
       * template method: allows you to build up info to show in the summary
       * displayed at the end of the application
       */
      override fun collectSummaryExtra(): List<Pair<String, String>>? {
          return listOf(
                  Pair(ctx.app.about.name, " extra 1  = extra summary data1"),
                  Pair(ctx.app.about.name, " extra 2  = extra summary data2")
          )
      }
  }

```

# Env
```kotlin

/**
 * The list of defaults environments to choose from.
 * An environment definition is defined by its name, mode
 * The key is built up from name and mode as {name}.{mode}
 * e.g. "qa1.QA"
 *
 * Each of these environments should map to an associated env.{name}.conf
 * config file in the /resources/ directory. But there is no dependency
 * on this Env component to a Config component
 *
 * e.g.
 * /resources/env.conf     ( common      config )
 * /resources/env.loc.conf ( local       config )
 * /resources/env.dev.conf ( development config )
 * /resources/env.qa1.conf ( qa1         config )
 * /resources/env.qa2.conf ( qa2         config )
 * /resources/env.stg.conf ( staging     config )
 * /resources/env.pro.conf ( production  config )
 *
 * @return
 */
fun defaults(): Envs =
	Envs(listOf(
        Env("loc", Dev , desc = "Dev environment (local)"),
        Env("dev", Dev , desc = "Dev environment (shared)"),
        Env("qa1", Qa  , desc = "QA environment  (current release)"),
        Env("qa2", Qa  , desc = "QA environment  (last release)"),
        Env("stg", Uat , desc = "STG environment (demo)"),
        Env("pro", Prod, desc = "LIVE environment")
    ))
```


# Args

```kotlin
 java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt -conf.dir='file://./conf/sampleapp-batch/'
 java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt -env=dev -log.level=info -config.location = "jars"
 java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt -env=dev -log.level=info -config.location = "conf"
 java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt -env=dev -log.level=info -config.location = "file://./conf-sample-batch"
 java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt --version
 java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt --about
 java -cp "%APP_CLASSPATH%" slatekit.sampleapp.batch.SampleAppBatchKt ?

```


# Environment
```kotlin


    /**
     * Life-cycle execution hook: for your app to perform the main logic
     * 
     * @return
     */
    override fun onExecute(): Result<Any> {

        // Get access to context which has everything ( e.g env name )
        println("environment is: " + ctx.env.name)

        // Execute your work here.
        info("app executing now")

        return ok()
    }
```


# Schema
```kotlin 
// APPROACH 2: Automatically build the AppContext using the AppRunner.build function
// that will check the command line args for selected environment and other info
// 1. args      : command line arguments
// 2. enc       : the encryptor to handle encryption and decryption of args/settings etc.
// 3. schema    : the schema representing allowed command line arguments
// 4. converter : a callback to convert/modify the application 1 last time before it is
//                finally supplied to your SampleApp constructor.
// NOTES:
// - Ensure your config files are available e.g. resources/env.conf
// - Env : By default, the first supported environment is used which is local "env.local"
// - Conf: By default, the config file associated w/ the environment is loaded "env.local.conf"
// - You can store info about the your app in your config file and that can be loaded.
val res = AppRunner.run ( 
    SampleApp (
        AppRunner.build (
            args      = args,
            enc       = Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
            schema    =  ArgsSchema()
                        .text("env"      , "the environment ", false, "dev"  , "dev"  , "loc|dev|qa1" )
                        .text("log.level", "the log level"   , false, "info" , "info" , "debug|info")
        )
    )
)

return res
```


# About 
```kotlin
val ctx = AppContext(
                arg = Args.default(),
                env = conf.env(),
                cfg = conf,
                log = LoggerConsole(),
                ent = Entities(),
                dbs = null,
                enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292"),
                inf = About(
                        id = "slatekit.examples",
                        name = "Slate Sample App",
                        desc = "Sample to show the base application with manually built context",
                        company = "slatekit",
                        version = "0.9.1",
                        contact = "kishore@abc.co",
                        region = "",
                        group = "",
                        url = "",
                        tags = "",
                        examples = ""
                ),
                state = success(true, "manually built")
        )
```

# Output
```kotlin
   
      /**
       * template method: allows you to build up info to show in the summary
       * displayed at the end of the application
       */
      override fun collectSummaryExtra(): List<Pair<String, String>>? {
          return listOf(
                  Pair(ctx.app.about.name, " extra 1  = extra summary data1"),
                  Pair(ctx.app.about.name, " extra 2  = extra summary data2")
          )
      }
```


# Log 
```bat
 Info  : app executing now
 Info  : app completed
 Info  : app shutting down
 Info  : ===============================================================
 Info  : SUMMARY :
 Info  : ===============================================================
 Info  : name              = Slate Sample App
 Info  : desc              = Sample to show the base application
 Info  : version           = 0.9.1
 Info  : tags              =
 Info  : group             =
 Info  : region            =
 Info  : contact           = kishore@abc.co
 Info  : url               =
 Info  : args              =
 Info  : env               = dev
 Info  : config            = env.conf
 Info  : log               = local:dev
 Info  : started           = 2017-07-11T11:54:13.132-04:00[America/New_York]
 Info  : ended             = 2017-07-11T11:54:18.408-04:00[America/New_York]
 Info  : duration          = PT5.276S
 Info  : status            = ended
 Info  : errors            = 0
 Info  : error             = n/a
 Info  : host.name         = KRPC1
 Info  : host.ip           =
 Info  : host.origin       = Windows 10
 Info  : host.version      = 10.0
 Info  : lang.name         = kotlin
 Info  : lang.version      = 1.8.0_91
 Info  : lang.vendor       = Oracle Corporation
 Info  : lang.java         = local
 Info  : lang.home         = C:/Tools/Java/jdk1.8.0_91/jre
 Info  : Slate Sample App =  extra 1  = extra summary data1
 Info  : Slate Sample App =  extra 2  = extra summary data2
 Info  : ===============================================================

```