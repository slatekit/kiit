
# Info

<table class="table table-striped table-bordered">
  <tbody>
    <tr>
      <td><strong>desc</strong></td>
      <td>Get/Set useful diagnostics about the system, language runtime, application and more</td>
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
      <td>slatekit.common.info</td>
    </tr>
    <tr>
      <td><strong>source core</strong></td>
      <td>slatekit.common.info.About.kt</td>
    </tr>
    <tr>
      <td><strong>source folder</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/info" class="url-ch">src/lib/kotlin/slatekit-common/src/main/kotlin/slatekit/common/info</a></td>
    </tr>
    <tr>
      <td><strong>example</strong></td>
      <td><a href="https://github.com/code-helix/slatekit/tree/master/src/lib/kotlin/slatekit-examples/src/main/kotlin/slatekit/examples/Example_Info.kt" class="url-ch">src/lib/kotlin/slate-examples/src/main/kotlin/slatekit/examples/Example_Info.kt</a></td>
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
import slatekit.common.info.About
import slatekit.common.info.Host
import slatekit.common.info.Lang
import slatekit.common.info.StartInfo


// optional 
import slatekit.core.cmds.Cmd
import slatekit.results.Try
import slatekit.results.Success
import slatekit.common.envs.EnvMode




{{< /highlight >}}

## Setup
{{< highlight kotlin >}}


n/a


{{< /highlight >}}

## Usage
{{< highlight kotlin >}}


    // CASE 1: Get the host info
    val host = Host.local()
    host.each { name, value -> println( "$name : $value" ) }
    println()


    // CASE 2: Get the Lang runtime info ( java version, kotlin version etc )
    val lang = Lang.kotlin()
    lang.each { name, value -> println( "$name : $value" ) }
    println()


    // CASE 3: Set startup info ( env, config, log, args)
    val startup = StartInfo("-level=error", "{@app}-{@env}-{@date}.log", "{@app}.config", EnvMode.Qat.name)
    startup.each { name, value -> println( "$name : $value" ) }
    println()


    // CASE 4: Set up info about your application.
    val app = About(
      id = "slatekit.examples",
      name = "My sample app",
      desc = "Sample app using Slate Kit",
      company = "slatekit",
      region = "usa.ny",
      version = "1.0.1.3",
      tags = "api,slate,app",
      group = "product division",
      url = "http://products.myapp.com",
      contact = "kishore@codehelix.co",
      examples = "myapp.exe -env=dev -level=info"
    )
    app.log( { name, value -> println( "${name} : ${value}" ) } )
    

{{< /highlight >}}



## Output

{{< highlight kotlin >}}
  // HOST INFO
  api : KRPC1
  ip : Windows 10
  origin : local
  arch : amd64
  version : 10.0
  ext1 : C:/Users/kv/AppData/Local/Temp/

  // LANGUAGE INFO
  api : kotlin
  home : C:/Tools/Java/jdk1.8.0_91/jre
  versionNum : 2.11.7
  version : 1.8.0_91
  origin : local
  ext1 :

  // STARTUP INFO
  args : Some([Ljava.lang.String;11c20519)
  log : {app}-{env}-{date}.log
  config : {app}.config
  env : qa

  // APP INFO
  api     : My sample app
  desc     : Sample app using Slate Kit
  group    : product division
  region   : usa.ny
  url      : "http://products.myapp.com"
  contact  : kishore@codehelix.co
  version  : 1.0.1.3
  tags     : api,slate,app
  examples : myapp.exe -env=dev -level=info
{{< /highlight >}}
  