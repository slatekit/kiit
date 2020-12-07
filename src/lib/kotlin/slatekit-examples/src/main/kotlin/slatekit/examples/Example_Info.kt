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
import slatekit.common.info.About
import slatekit.common.info.Host
import slatekit.common.info.Lang
//</doc:import_required>

//<doc:import_examples>

import slatekit.results.Try
import slatekit.results.Success


//</doc:import_examples>


class Example_Info  : Command("info") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
    // CASE 1: Get the host info
    val host = Host.local()
    host.each { name, value -> println( "$name : $value" ) }
    println()


    // CASE 2: Get the Lang runtime info ( java version, kotlin version etc )
    val lang = Lang.kotlin()
    lang.each { name, value -> println( "$name : $value" ) }
    println()

    // CASE 3: Set up info about your application.
    val app = About(
      area = "product1",
      name = "My sample app",
      desc = "Sample app using Slate Kit",
      company = "slatekit",
      region = "usa.ny",
      version = "1.0.1.3",
      tags = "api,slate,app",
      url = "http://products.myapp.com",
      contact = "kishore@codehelix.co",
      examples = "myapp.exe -env=dev -level=info"
    )
    app.log( { name, value -> println( "${name} : ${value}" ) } )
    //</doc:examples>

    return Success("")
  }
}
/*

  //<doc:output>
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
  //</doc:output>
* */
