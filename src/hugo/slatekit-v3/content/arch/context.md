---
title: "Context"
date: 2019-11-17T23:55:41-05:00
section_header: Context
---

# Overview
The Context is a container for common application dependencies such as the parsed command line **args**, selected **environment**, **config** properties, **logs**, **encryptor**, **app, build, host** info and more. This is created and must be available for any runnable application such as a **Console App, CLI, or Server**. It is simply an interface with the following components, and there are default implementations, and builders available for convenience.

{{< highlight kotlin >}}

    /**
     - Represents context of a running application and contains information used for most components
     - args : command line arguments
     - envs : environment selection ( dev, qa, staging, prod )
     - conf : config settings
     - logs : logger
     - info : info about the application
     - enc  : encryption/decryption service
     - dirs : directories used for the app
     */
    interface Context {
        val arg: Args
        val env: Env
        val cfg: Conf
        val logs: Logs
        val info: Info
        val enc: Encryptor?
        val dirs: Folders?
    }

{{< /highlight  >}}

{{% break %}}

# Index
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Section</strong></td>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1</strong></td>
        <td><strong><a class="url-ch" href="arch/context#status">Status</a></strong></td>
        <td>Current status of this component</td>
    </tr>
    <tr>
        <td><strong>2</strong></td>
        <td><strong><a class="url-ch" href="arch/context#install">Install</a></strong></td>
        <td>Installation instructions and references to sources</td>
    </tr>
    <tr>
        <td><strong>3</strong></td>
        <td><strong><a class="url-ch" href="arch/context#requires">Requires</a></strong></td>
        <td>Lists all the Slate Kit and third-party dependencies</td>
    </tr>
    <tr>
        <td><strong>4</strong></td>
        <td><strong><a class="url-ch" href="arch/context#sample">Sample</a></strong></td>
        <td>Quick sample to show usage of the component</td>
    </tr>
    <tr>
        <td><strong>5</strong></td>
        <td><strong><a class="url-ch" href="arch/context#goals">Goals</a></strong></td>
        <td>Goals of this component and the problems it attempts to solve</td>
    </tr>
    <tr>
        <td><strong>6</strong></td>
        <td><strong><a class="url-ch" href="arch/context#concepts">Concepts</a></strong></td>
        <td>Core concepts to understand in this component</td>
    </tr>
    <tr>
        <td><strong>7</strong></td>
        <td><strong><a class="url-ch" href="arch/context#features">Features</a></strong></td>
        <td>List all the features supported</td>
    </tr>
    <tr>
        <td><strong>8</strong></td>
        <td><strong><a class="url-ch" href="arch/context#setup">Setup</a></strong></td>
        <td>Set up and configure this component for use</td>
    </tr>
    <tr>
        <td><strong>9</strong></td>
        <td><strong><a class="url-ch" href="arch/context#details">Details</a></strong></td>
        <td>In-depth examples of the supported features</td>
    </tr>
</table>

{{% section-end mod="arch/context" %}}

# Status
This component is currently stable, has default implementations, and can be used for both **Android and Server**

{{% section-end mod="arch/context" %}}

# Install
{{< highlight groovy >}}

    repositories {
        // other repositories
        maven { url  "http://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other dependencies ...

        compile 'com.slatekit:slatekit-common:1.0.0'
    }

{{< /highlight >}}

{{% sk-module 
    name="App"
    package="slatekit.common"
    jar="slatekit.common.jar"
    git="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/Context.kt"
    gitAlias="slatekit/src/lib/kotlin/slatekit-common"
    url="core/app"
    uses="slatekit.results, slatekit.common"
    exampleUrl="Example_Context.kt"
    exampleFileName="Example_Context.kt"
%}}

{{% section-end mod="arch/context" %}}

# Requires
This component uses several components from the Slate Kit Utilities.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Component</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><a class="url-ch" href="utils/overview">Slate Kit - Common</a></td>
        <td>Common utilities for both android + server</td>
    </tr>
    <tr>
        <td><a class="url-ch" href="utils/args">Args</a></td>
        <td>Command line arguments parser</td>
    </tr>
    <tr>
        <td><a class="url-ch" href="utils/env">Env</a></td>
        <td>Environment setup and selector</td>
    </tr>
    <tr>
        <td><a class="url-ch" href="utils/config">Conf</a></td>
        <td>Config settings with inheritance</td>
    </tr>
    <tr>
        <td><a class="url-ch" href="utils/logger">Logs</a></td>
        <td>Logger and logging support</td>
    </tr>
</table>

{{% section-end mod="arch/context" %}}

# Sample
The context can be constructed manually or using convenience methods that build the context from the command line args, and configs.
{{< highlight kotlin >}}
    
    import slatekit.common.CommonContext

    // Create simple context
    val ctx1 = CommonContext.simple("demoapp")

{{< /highlight >}}

{{% section-end mod="arch/context" %}}

# Goals
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Goal</strong></td>
        <td><strong>Description</strong></td>
    </tr>
    <tr>
        <td><strong>1. Defaults</strong></td>
        <td>Provide sensible defaults to common dependencies like args, env, conf, logs and more for runnable apps</td>
    </tr>
    <tr>
        <td><strong>2. Awareness</strong> </td>
        <td>Provides access to application environment, configs, build, host info to serve as an identity for the app.</td>
    </tr>
    <tr>
        <td><strong>3. Extensible</strong></td>
        <td>Can be extended so that you can build your own context and/or load addition components.</td>
    </tr>
</table>

{{% section-end mod="arch/context" %}}

# Setup 
Some examples of setting up the context
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Example</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Simple</strong></td>
        <td>Build a simple context using convenience methods</td>
        <td><a href="arch/context/#simple-setup" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>2. Manual</strong> </td>
        <td>Manually build the context with explicit values</td>
        <td><a href="arch/context/#manual-setup" class="more"><span class="btn btn-primary">more</span></a></td>                                         
    </tr>
    <tr>
        <td><strong>3. Derived</strong></td>
        <td>Allow the context to the built by the Application Runner</td>
        <td><a href="arch/context/#derived-setup" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
</table>

{{% section-end mod="arc/context" %}}

##  Simple {#simple-setup}
The context can be constructed using convenience methods that build the context using empty/default data.
{{< highlight kotlin >}}
    
    import slatekit.common.CommonContext

    // Create simple context
    val ctx1 = CommonContext.simple("demoapp")

{{< /highlight >}}

##  Manual {#manual-setup}
The context can be constructed explicitly by supplying all the inputs
{{< highlight kotlin >}}
    
    import slatekit.common.CommonContext

    // Create simple context
    val ctx2 = CommonContext(
            args = Args.default(),
            envs = Envs.defaults(),
            conf = Config(),        // Loads resources/env.conf
            logs = LogsDefault,
            info = Info(
                    About(
                        area = "department1",
                        name = "sample-app-1",
                        desc = "Sample application 1",
                        company = "Company 1",
                        region = "New York",
                        url = "http://company1.com/dep1/sampleapp-1",
                        contact = "dept1@company1.com",
                        version = "1.0.1",
                        tags = "sample app slatekit",
                        examples = ""
                    ),
                    Build.empty,
                    Sys.build()
            )
    )

{{< /highlight >}}

##  Derived {#derived-setup}
The context is automatically created when using the {{% sk-link-arch name="app" %}}.
In this case, the AppRunner inspects the command line args, config settings from **env.conf** and then builds up the context. You also supply the builder function that supplies an instance of your Application using the auto-created context. You can modify/copy the context here before it is finally passed to the Application constructor.
{{< highlight kotlin >}}
    
    import slatekit.common.CommonContext

    // Create simple context
    AppRunner.run(
            rawArgs = request.args.raw.toTypedArray(),
            schema = ArgsSchema(),
            enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
            logs = LogbackLogs(),
            about = About.none,
            builder = { ctx -> SampleApp(ctx) }
    )

{{< /highlight >}}

{{% section-end mod="arch/context" %}}


# Features
Most applications ( whether they are console, cli, jobs, server ) require basic boiler plate setup and access to services. This Context fills that need by providing these core services.
<table class="table table-bordered table-striped">
    <tr>
        <td><strong>Name</strong></td>
        <td><strong>Description</strong></td>
        <td><strong>More</strong></td>
    </tr>
    <tr>
        <td><strong>1. Args</strong></td>
        <td>Access to parsed command line arguments</td>
        <td><a href="arch/context/#args" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>2. Env</strong> </td>
        <td>Access to selected environment</td> 
        <td><a href="arch/context/#env" class="more"><span class="btn btn-primary">more</span></a></td>                    
    </tr>
    <tr>
        <td><strong>3. Conf</strong></td>
        <td>Access to current config properties</td>
        <td><a href="arch/context/#conf" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>4. Logs</strong></td>
        <td>Access to Log factory</td>
        <td><a href="arch/context/#logs" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>5. Enc</strong></td>
        <td>Access to encryptor</td>
        <td><a href="arch/context/#encrypt" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>6. Build</strong></td>
        <td>Access to current build information</td>
        <td><a href="arch/context/#build" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
    <tr>
        <td><strong>7. About</strong></td>
        <td>Access to current application information</td>
        <td><a href="arch/context/#about" class="more"><span class="btn btn-primary">more</span></a></td>
    </tr>
</table>
{{% section-end mod="arch/context" %}}


## Args {#args}
You can access the parsed command line args. 
See {{% sk-link-util page="args" name="Args" %}} for more info.
{{< highlight kotlin >}}
      
    ctx.args.line       
    ctx.args.action      // e.g. "action" if using "service.action -key=1"
    ctx.args.get("env") 
    ctx.args.getStringOrNull("log.level")
    ctx.args.getStringOrElse("log.level", "warn")
    
{{< /highlight >}}
{{% feature-end mod="arch/context" %}}

## Envs {#env}
You have access to the environments and currently selected environment.
See {{% sk-link-util page="env" name="Env" %}} for more info.
{{< highlight kotlin >}}
      
    ctx.envs.name    // "loc" ( representing local )
    ctx.envs.env     // "dev" EnvMode: ( Dev | Qat | Uat | Pro )
    ctx.envs.key     // "loc:dev"  {name}:{env}
    ctx.envs.current // current Env object
    ctx.envs.all     // list of all Env environments
     

{{< /highlight >}}
{{% feature-end mod="arch/context" %}}

## Conf {#conf}
You have access to the currently loaded configuration settings.
See {{% sk-link-util page="config" name="Conf" %}} for more info.
{{< highlight kotlin >}}
     
    ctx.conf.getInt("paging.batchSize")
    ctx.conf.getIntOrNull("paging.batchSize")
    ctx.conf.getIntOrElse("paging.batchSize", 4)
    
{{< /highlight >}}
{{% feature-end mod="arch/context" %}}

## Logs {#logs}
You have have to the logs/factory to create loggers.
See {{% sk-link-util page="logger" name="Logs" %}} for more info.
{{< highlight kotlin >}}
     
    val logger1 = ctx.logs.getLogger()
    val logger2 = ctx.logs.getLogger(name = "service1")
    val logger3 = ctx.logs.getLogger(Example_Jobs::class.java)
    logger1.level
    logger1.name
    logger1.debug("Debug message")
    logger1.info ("Info message")
    logger1.warn ("Warn message")
    logger1.error("Error message")
    logger1.fatal("Fatal message")
    
{{< /highlight >}}
{{% feature-end mod="arch/context" %}}

## Enc {#enc}
You can access the optional encryptor to encrypt/decrypt data.
See {{% sk-link-util page="encrypt" name="Encryptor" %}} for more info.
{{< highlight kotlin >}}
      
    ctx.enc?.encrypt("raw text")
    ctx.enc?.decrypt("")
     
{{< /highlight >}}
{{% feature-end mod="arch/context" %}}

{{% section-end mod="arch/context" %}}

