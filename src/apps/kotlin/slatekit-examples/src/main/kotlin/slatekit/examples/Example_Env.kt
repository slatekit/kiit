/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.common.envs.*

//</doc:import_required>

//<doc:import_examples>


import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>


class Example_Env  : Command("env") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>

    // CASE 1: Build a list of environments
    val envs1 = Envs(listOf(
            Env("loc", EnvMode.Dev , desc = "Dev environment (local)"),
            Env("dev", EnvMode.Dev , desc = "Dev environment (dev)"),
            Env("qat", EnvMode.Qat , desc = "QAT environment (test)"),
            Env("stg", EnvMode.Uat , desc = "STG environment (demo)"),
            Env("pro", EnvMode.Pro , desc = "LIVE environment")
    ))

    // CASE 2: Use the default list of environments ( same as above )
    val envs = slatekit.common.envs.Envs.defaults()

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

