
# Env

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Environment selector and validator for environments such as (local, dev, qa, stg, prod) )</td>
    </tr>
    <tr>
      <td><strong>date</strong></td>
      <td>2019-03-15</td>
    </tr>
    <tr>
      <td><strong>version</strong></td>
      <td>0.9.9</td>
    </tr>
    <tr>
      <td><strong>jar</strong></td>
      <td>slatekit.common.jar</td>
    </tr>
    <tr>
      <td><strong>namespace</strong></td>
      <td>slatekit.common.envs</td>
    </tr>
    <tr>
      <td><strong>source core</strong></td>
      <td>slatekit.common.envs.Env.kt</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/envs" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/envs</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Env.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Env.kt</a></td>
    </tr>
    <tr>
      <td><strong>depends on</strong></td>
      <td></td>
    </tr>
  </tbody>
</table>



## Import
{{< highlight kotlin >}}


// required 
import slatekit.common.envs.*



// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.Try
import slatekit.results.Success




{{< /highlight >}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}

## Usage
{{< highlight kotlin >}}



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
    

{{< /highlight >}}


