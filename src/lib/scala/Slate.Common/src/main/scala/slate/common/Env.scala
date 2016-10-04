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

package slate.common


/**
  * Store the currently selected environment ( local, dev, qa, stg, prod ) and provides some
  * utility functions to parse an environment
  */
object Env extends EnvSupport {
  val PROD = "pro"
  val DEV = "dev"
  val QA  = "qa"
  val UAT = "uat"

  private var _envItem = EnvItem("loc", Env.DEV)


  /**
    * Name of the currently selected environment e.g. ( dev1, qa1, qa2, beta, prod )
    * @return
    */
  def name: String = _envItem.name


  /**
    * Mode of the currently selected environment ( dev, qa, uat, pro )
    * @return
    */
  def mode: String = _envItem.env


  /**
    * The fully qualified name of the currently selected environment ( combines the name + key )
    * @return
    */
  def key: String = _envItem.key


  /**
    * Sets the current environment
    * @param env
    */
  def set(env:EnvItem): Unit =
  {
    _envItem = env
  }


  /**
    * Gets the current environment
    * @return
    */
  def current = _envItem


  /**
    * whether the current environment matches the environment name supplied.
    * @param env
    * @return
    */
  override def isEnv(env: String):Boolean =
  {
    _envItem.env == env
  }


  /**
    * parses the environment name e.g. "qa1:qa" = name:mode
    * @param env
    * @return
    */
  def parse(env:String):EnvItem = {
    val tokens = Strings.split(env, ':')

    // e.g. "dev1", "dev", dev1:dev")
    if(tokens.size == 1){
      return EnvItem(tokens(0), tokens(0))
    }
    EnvItem(tokens(0), tokens(1))
  }
}
