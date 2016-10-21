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


package slate.examples

//<doc:import_required>
import slate.common.console.ConsoleWriter
import slate.common.envs._
import slate.common.results.ResultSupportIn
import slate.common.{Ensure, ListMap, Todo}
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
//</doc:import_examples>


class Example_Env  extends Cmd("types") with ResultSupportIn {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>

    // CASE 1: Build a list of environments
    val envs1 = new Envs(List[EnvItem](
      EnvItem("loc", Env.DEV , desc = "Dev environment (local)" ),
      EnvItem("dev", Env.DEV , desc = "Dev environment (shared)" ),
      EnvItem("qa1", Env.QA  , desc = "QA environment  (current release)" ),
      EnvItem("qa2", Env.QA  , desc = "QA environment  (last release)" ),
      EnvItem("stg", Env.UAT , desc = "STG environment (demo)" ),
      EnvItem("pro", Env.PROD, desc = "LIVE environment" )
    ))

    // CASE 2: Use the default list of environments ( same as above )
    val envs = Env.defaults()

    // CASE 3: Get one of the environments by name
    val qa1 = envs("qa1")
    println( qa1 )

    // CASE 4: Validate one of the environments by name
    println( envs.isValid("qa2") )

    // CASE 5: Current environment ( nothing - none selected )
    println( envs.current.isDefined )

    // CASE 6: Select an environment
    val envs2 = envs.select("dev")
    println( envs2 )

    // CASE 7: Get info about currently selected environment
    println( envs2.name    )
    println( envs2.isDev   )
    println( envs2.isQa    )
    println( envs2.isUat   )
    println( envs2.isProd  )
    println( envs2.current )
    //</doc:examples>

    ok()
  }


  /*
  //<doc:output>
```java
  TITLE IS IN CAPS
  subtitle is in color cyan
  url is in blue
  highlight is in color
  important is red
  error shown in red
  success is in green

  Key
  Name =  Superman


  ABOUT APP
  Example of Console component
  http://www.slatekit.com
  visit us for more info

```
  //</doc:output>
  */
}

