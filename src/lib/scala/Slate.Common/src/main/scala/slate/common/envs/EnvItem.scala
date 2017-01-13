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

case class EnvItem( name:String, env:String, region:String = "", desc:String = "" ) extends EnvSupport {

  /**
    * "qa1:qa"
    * @return
    */
  def key : String = name + ":" + env


  override def isEnv(envMode: String):Boolean = env == envMode
}