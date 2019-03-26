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
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.apis.support.ApiWithSupport
import slatekit.common.Context
import slatekit.common.Vars
import slatekit.core.sms.SmsService
import slatekit.results.Try

@Api(area = "cloud", name = "sms", desc = "api to send sms",
        auth = AuthModes.apiKey, roles = "ops", verb = Verbs.auto, protocol = Protocols.all)
class SmsApi(val svc: SmsService, override val context: Context) : ApiWithSupport {
    /**
     * sends a message
     * @param message : message to send
     * @param countryCode : destination phone country code
     * @param phone : destination phone
     */
    @ApiAction(desc = "send an sms")
    fun send(message: String, countryCode: String, phone: String): Try<Boolean> {
        return this.svc.send(message, countryCode, phone).toTry()
    }

    /**
     * sends a message using the template and variables supplied
     * @param name : name of the template
     * @param countryCode : destination phone country code
     * @param phone : destination phone
     * @param vars : values to replace the variables in template ( extra args on command line
     *                      will be automatically added into this collection )
     */
    @ApiAction(desc = "send an sms using a template")
    fun sendUsingTemplate(name: String, countryCode: String, phone: String, vars: Vars): Try<Boolean> {
        return this.svc.sendUsingTemplate(name, countryCode, phone, vars).toTry()
    }
}
