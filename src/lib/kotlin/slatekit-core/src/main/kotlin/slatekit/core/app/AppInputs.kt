/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.core.app

import slatekit.common.args.Args
import slatekit.common.conf.ConfigBase
import slatekit.common.envs.Env


data class AppInputs(val args: Args,
                     val env: Env,
                     val confBase: ConfigBase,
                     val confEnv: ConfigBase) {
}
