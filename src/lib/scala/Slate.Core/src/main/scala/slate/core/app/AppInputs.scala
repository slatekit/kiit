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
package slate.core.app

import slate.common.args.Args
import slate.common.conf.ConfigBase
import slate.common.envs.Env

case class AppInputs(args:Args, env:Env, confBase:ConfigBase, confEnv:ConfigBase) {

}
