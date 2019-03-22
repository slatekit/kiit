
# Ctx

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>An application context to contain common dependencies such as configs, logger, encryptor, etc, to be accessible to other components</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-22</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.17</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.core.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.core.common.AppContext</td>
    </tr>
    <tr>
      <td><strong>artifact</strong></td>
      <td>com.slatekit:slatekit-core</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-core/src/main/kotlin/slatekit/core/common/AppContext" class="url-ch">src/lib/kotlin/slatekit-core/src/main/kotlin/slatekit/core/common/AppContext</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Context.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Context.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td> slatekit-results slatekit-common</td>
    </tr>
  </tbody>
</table>
{{% break %}}

## Gradle
{{< highlight gradle >}}
    // other setup ...
    repositories {
        maven { url  "https://dl.bintray.com/codehelixinc/slatekit" }
    }

    dependencies {
        // other libraries

        // slatekit-common: Utilities for Android or Server
        compile 'com.slatekit:slatekit-core:0.9.17'
    }

{{< /highlight >}}
{{% break %}}

## Import
{{< highlight kotlin >}}


// required 
import slatekit.app.AppFuncs
import slatekit.core.common.AppContext


// optional 
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.conf.Config
import slatekit.common.encrypt.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.common.info.*
import slatekit.common.log.LogsDefault
import slatekit.app.AppRunner
import slatekit.common.Context
import slatekit.entities.core.Entities
import slatekit.core.cmds.Cmd
import slatekit.db.Db
import slatekit.results.StatusCodes
import slatekit.results.Try
import slatekit.results.Success
import slatekit.results.getOrElse




{{< /highlight >}}
{{% break %}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}
{{% break %}}

## Usage
{{< highlight kotlin >}}



        // OVERVIEW:
        // The AppContext is a container for common dependencies
        // across different components in an application.
        // The main design goal of the context is to contain
        // many of the dependencies in 1 object so they can be
        // easily passed around where needed and made accessible.
        //
        // INCLUDED:
        // 1.  args: parsed command line arguments
        // 2.  env : the selected environment ( dev, qa, uat, prod )
        // 3.  conf: the config settings
        // 4.  log : the global logger ( you can easily have local loggers )
        // 5.  inf : info about the application (name, desc, group, etc)
        // 6.  ent : the entities which are mapped ORM entities ( optional )
        // 7.  sys : the system info including host and language
        // 8.  dirs: the standardized runtime folders for the app
        // 9.  enc : the encryption service to handle encryption/decryption
        // 10. app : the application info/about
        //
        // NOTES:
        // 1. Many of these are OPTIONAL
        // 2. It is implemented as a data class
        // 3. The Context is an interface defined in slatekit.common.Context
        // 4. The AppContext is an implementation defined in slatekit.core.common.AppContext
        // 5. To customize the context for different components, you
        //    either extend the Context, and/or copy the AppContext
        //    with modifications

        // CASE 1: Build a simple context with minimal info that includes:
        // - default arguments ( command line )
        // - dev environment
        // - Config() representing conf settings from "env.conf"
        // - default logger ( console )
        // - entities ( registrations for orm )
        val ctx1 = AppContext(
            arg = Args.default(),
            env = Env("dev", EnvMode.Dev, "ny", "dev environment"),
            cfg = Config(),
            logs = LogsDefault,
            ent = Entities({con -> Db(con) }),
            sys = Sys.build(),
            build = Build.empty,
            start = StartInfo.none,
            app = About(
                    id = "sample-app-1",
                    name = "Sample App-1",
                    desc = "Sample application 1",
                    company = "Company 1",
                    group = "Department 1",
                    region = "New York",
                    url = "http://company1.com/dep1/sampleapp-1",
                    contact = "dept1@company1.com",
                    version = "1.0.1",
                    tags = "sample app slatekit",
                    examples = ""
            )
        )

        // CASE 2: Typically your application will want to derive the
        // context from either the command line args and or the config
        // There is a builder method takes command line arguments and
        // other inputs and constructs the context. This example shows
        // only providing the arguments to build the context
        //
        // NOTE: This checks for "-env" arg and loads the corresponding
        // inherited config environment (refer to config in utils for more info )
        // but basically, this loads the env.dev.conf with fallback to env.conf
        // 1. "env.dev.conf" ( environment specific )
        // 2. "env.conf"     ( common / base line   )

        // CASE 2 : This example shows providing the args schema for parsing the args
        // refer to Args in utils for more info.
        // NOTE: There are additional parameters on the build function ( callbacks )
        // to allow you to get the context and modify it before it is returned.
        val ctx3 = AppFuncs.context(
                    args   = Args.parse("-env=dev -log -log.level=debug").getOrElse { Args.default() },
                    enc    = Encryptor("wejklhviuxywehjk", "3214maslkdf03292", B64Java8),
                    schema = ArgsSchema()
                            .text("env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                            .text("region", "the region linked to app", false, "us", "us", "us|europe|india|*")
                            .text("config.loc", "location of config files", false, "jar", "jar", "jar|conf")
                            .text("log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error"),
                    about  = About("app id", "sample app", "app desc"),
                    logs   = LogsDefault
                )
        ctx3.onSuccess {
            showContext(it)
        }

        // CASE 4: You can also build an error context representing an invalid context
        val ctx4 = AppContext.err(StatusCodes.BAD_REQUEST.code, "Bad context, invalid inputs supplied")
        showContext(ctx4)

        

{{< /highlight >}}
{{% break %}}

