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

import slate.common.results.ResultSupportIn
import slate.common.{Strings, Result, envs}


/**
  * Store the currently selected environment ( local, dev, qa, stg, prod ) and provides some
  * utility functions to parse an environment
  */
case class Envs(current:EnvItem, all:List[EnvItem]) extends EnvSupport with ResultSupportIn {


  /**
   * Name of the currently selected environment e.g. ( dev1, qa1, qa2, beta, prod )
   * @return
   */
  def name: String = current.name


  /**
   * Mode of the currently selected environment ( dev, qa, uat, pro )
   * @return
   */
  def mode: String = current.env


  /**
   * The fully qualified name of the currently selected environment ( combines the name + key )
   * @return
   */
  def key: String = current.key


  /**
   * whether the current environment matches the environment name supplied.
   * @param env
   * @return
   */
  override def isEnv(env: String): Boolean = {
    current.env == env
  }


  /**
   * selects a new environment and returns a new Envs collection with the s
   * selected environment
   * @param name : Name of the environment
   * @return
   */
  def select(name:String): Envs = {
    val matched = all.filter( item => Strings.isMatch(item.name, name ))
    new Envs(matched.head, all)
  }


  /**
   * validates the environment against the supported
   *
   * @param env
   * @return
   */
  def validate(env:envs.EnvItem): Result[envs.EnvItem] = {
    val matched = all.filter( item => Strings.isMatch(item.name, env.name ))
    if(matched != null && matched.size > 0)
      return success(matched.head)

    failure(Some(s"Unknown environment name : ${env.name} supplied"))
  }
}