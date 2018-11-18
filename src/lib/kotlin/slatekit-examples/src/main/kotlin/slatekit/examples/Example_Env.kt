/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.common.envs.*

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.ResultEx
import slatekit.common.Success

//</doc:import_examples>


class Example_Env  : Cmd("env") {

  override fun executeInternal(args: Array<String>?) : ResultEx<Any>
  {
    //<doc:examples>

    // CASE 1: Build a list of environments
    val envs1 = Envs(listOf(
            Env("loc", EnvMode.Dev , desc = "Dev environment (local)"),
            Env("dev", EnvMode.Dev , desc = "Dev environment (shared)"),
            Env("qa1", EnvMode.Qat , desc = "QA environment  (current release)"),
            Env("qa2", EnvMode.Qat , desc = "QA environment  (last release)"),
            Env("stg", EnvMode.Uat , desc = "STG environment (demo)"),
            Env("pro", EnvMode.Pro , desc = "LIVE environment")
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
    println( envs2.isQat    )
    println( envs2.isUat   )
    println( envs2.isPro  )
    println( envs2.current )
    //</doc:examples>

    return Success("")
  }

}

