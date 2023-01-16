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
import kiit.apis.support.FileSupport
import kiit.context.Context
import kiit.common.Sources
import kiit.common.crypto.Encryptor
import kiit.common.info.Host
import kiit.common.log.Logger

@Api(area = "app", name = "version", desc = "api to get version information",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class VersionApi(override val context: Context) : FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()


    @Action(desc = "gets info about the host")
    fun host(): Host = context.info.host


    @Action(desc = "get the version of the application")
    fun app(): String = context.info.build.version


    @Action(desc = "gets the version of java")
    fun java(): String = context.info.lang.version


    @Action(desc = "gets the version of kotlin")
    fun kotlin(): String = context.info.lang.vendor


    @Action(desc = "gets the version of the system")
    fun version(): String = context.info.host.version
}