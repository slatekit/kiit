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

import slate.common.{Strings}


/**
  * Represents a system environment
  * @param name   : e.g. Quality Assurance
  * @param mode   : e.g. Qa
  * @param region : New york
  * @param desc   : Qa environment 1 in new york
  */
case class Env( name:String, mode:EnvMode, region:String = "", desc:String = "" ) extends EnvSupport {

  /**
    * "qa1:qa"
    * @return
    */
  def key : String = name + ":" + mode.name


  override def isEnv(envMode: String):Boolean = mode.name == envMode


  override def isEnv(envMode: EnvMode):Boolean = mode == envMode
}


object Env {

  /**
   * List of default environments supported in slate kit
   * @return
   */
  def defaults(): Envs = {
    val all = List[Env](
      Env("loc", Dev , desc = "Dev environment (local)" ),
      Env("dev", Dev , desc = "Dev environment (shared)" ),
      Env("qa1", Qa  , desc = "QA environment  (current release)" ),
      Env("qa2", Qa  , desc = "QA environment  (last release)" ),
      Env("stg", Uat , desc = "STG environment (demo)" ),
      Env("pro", Prod, desc = "LIVE environment" )
    )
    new Envs(all, Some(all(0)))
  }


  /**
   * parses the environment name e.g. "qa1:qa" = name:mode
   * @param env
   * @return
   */
  def parse(env:String):Env = {
    val tokens = Strings.split(env, ':')

    // e.g. "dev1", "dev", dev1:dev")
    if(tokens.length == 1)
      Env(tokens(0), interpret(tokens(0)))
    else
      Env(tokens(0), interpret(tokens(1)))
  }


  /**
    * interprets the string representation of an environment into the type object
    * @param mode
    * @return
    */
  def interpret(mode:String):EnvMode = {
    val m = Option(mode).getOrElse("").toLowerCase
    m match {
      case Dev.name  => Dev
      case Qa.name   => Qa
      case Uat.name  => Uat
      case Prod.name => Prod
      case Dis.name  => Dis
      case _         => Other(mode)
    }
  }

}
