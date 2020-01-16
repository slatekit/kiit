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

package slatekit.integration.apis

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.support.FileSupport
import slatekit.context.Context
import slatekit.common.Sources
import slatekit.common.Vars
import slatekit.common.encrypt.Encryptor
import slatekit.common.log.Logger
import slatekit.notifications.sms.SmsService
import slatekit.results.Outcome

@Api(area = "cloud", name = "sms", desc = "api to send sms",
        auth = AuthModes.KEYED, roles = ["ops"], verb = Verbs.AUTO, sources = [Sources.ALL])
class SmsApi(val svc: SmsService, override val context: Context) : FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    /**
     * sends a message
     * @param message : message to send
     * @param countryCode : destination phone country code
     * @param phone : destination phone
     */
    @Action(desc = "send an sms")
    suspend fun send(message: String, countryCode: String, phone: String): Outcome<Boolean> {
        return this.svc.send(message, countryCode, phone).map { true }
    }

    /**
     * sends a message using the template and variables supplied
     * @param name : name of the template
     * @param countryCode : destination phone country code
     * @param phone : destination phone
     * @param vars : values to replace the variables in template ( extra args on command line
     *                      will be automatically added into this collection )
     */
    @Action(desc = "send an sms using a template")
    suspend fun sendUsingTemplate(name: String, countryCode: String, phone: String, vars: Vars): Outcome<Boolean> {
        return this.svc.sendUsingTemplate(name, countryCode, phone, vars).map { true }
    }
}
