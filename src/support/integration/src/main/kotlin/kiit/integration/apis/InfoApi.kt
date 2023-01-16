/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.integration.apis

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import kiit.context.Context
import kiit.common.Sources
import kiit.requests.Request
import kiit.common.info.*

@Api(area = "app", name = "info", desc = "api info about the application and host",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class InfoApi(val context: Context)  {

    @Action(desc = "gets info about this build")
    fun build(): Build = context.info.build


    @Action(desc = "get info about the application")
    fun about(): About = context.info.about


    @Action(desc = "get info about the application")
    fun cmd(cmd: Request): About = context.info.about


    @Action(desc = "gets info about the language")
    fun lang(): Lang = context.info.lang


    @Action(desc = "gets info about the folders")
    fun dirs(): Folders = context.dirs ?: Folders.none

}
