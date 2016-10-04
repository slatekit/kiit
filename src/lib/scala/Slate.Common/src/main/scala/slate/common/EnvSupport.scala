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
package slate.common

trait EnvSupport {


  def isProd:Boolean =
  {
    isEnv(Env.PROD)
  }


  def isUat:Boolean =
  {
    isEnv(Env.UAT)
  }


  def isQa:Boolean =
  {
    isEnv(Env.QA)
  }


  def isDev:Boolean =
  {
    isEnv(Env.DEV)
  }


  def isEnv(envMode: String):Boolean = ???
}
