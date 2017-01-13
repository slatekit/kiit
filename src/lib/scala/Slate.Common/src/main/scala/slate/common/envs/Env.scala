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

package slate.common.envs

import slate.common.Strings

object Env {
  
  val PROD = "pro"
  val DEV  = "dev"
  val QA   = "qa"
  val UAT  = "uat"


  /**
   * List of default environments supported in slate kit
   * @return
   */
  def defaults(): Envs = {
    val all = List[EnvItem](
      EnvItem("loc", Env.DEV , desc = "Dev environment (local)" ),
      EnvItem("dev", Env.DEV , desc = "Dev environment (shared)" ),
      EnvItem("qa1", Env.QA  , desc = "QA environment  (current release)" ),
      EnvItem("qa2", Env.QA  , desc = "QA environment  (last release)" ),
      EnvItem("stg", Env.UAT , desc = "STG environment (demo)" ),
      EnvItem("pro", Env.PROD, desc = "LIVE environment" )
    )
    new Envs(all, Some(all(0)))
  }


  /**
   * parses the environment name e.g. "qa1:qa" = name:mode
   * @param env
   * @return
   */
  def parse(env:String):EnvItem = {
    val tokens = Strings.split(env, ':')

    // e.g. "dev1", "dev", dev1:dev")
    if(tokens.size == 1)
      EnvItem(tokens(0), tokens(0))
    else
      EnvItem(tokens(0), tokens(1))
  }

}
