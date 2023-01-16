/**
 *  <kiit_header>
 * url: www.slatekit.com
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
import kiit.common.values.Vars
import kiit.common.crypto.Encryptor
import kiit.common.log.Logger
import kiit.comms.sms.SmsService
import kiit.results.Outcome

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
        return this.svc.sendTemplate(name, countryCode, phone, vars).map { true }
    }
}
