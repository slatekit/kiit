/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.common.envs


trait EnvSupport {


  def isProd:Boolean = isEnv(Prod)


  def isUat:Boolean = isEnv(Uat)


  def isQa:Boolean = isEnv(Qa)


  def isDev:Boolean = isEnv(Dev)


  def isDis:Boolean = isEnv(Dis)


  def isEnv(mode: String):Boolean = ???


  def isEnv(mode: EnvMode):Boolean = ???
}
