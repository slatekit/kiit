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

abstract class EnvMode(val name:String)

/**
  * production
  */
case object Prod extends EnvMode("pro")


/**
  * Development
  */
case object Dev  extends EnvMode("dev")


/**
  * Quality assurance
  */
case object Qa   extends EnvMode("qa" )


/**
  * User Acceptance / Beta
  */
case object Uat  extends EnvMode("uat")


/**
  * Disaster recovery
  */
case object Dis  extends EnvMode("dr")


/**
  * Other environment mode
  * @param m
  */
case class Other(m:String) extends EnvMode(m)
