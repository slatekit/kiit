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
import slatekit.apis.support.ApiWithSupport
import slatekit.common.ResultMsg
import slatekit.common.Vars
import slatekit.core.common.AppContext
import slatekit.core.sms.SmsService


@Api(area = "cloud", name = "sms", desc = "api to send sms", roles = "ops", auth = "key-roles", verb = "*", protocol = "*")
class SmsApi(val svc: SmsService, override val context: AppContext) : ApiWithSupport {
    /**
     * sends a message
     * @param message     : message to send
     * @param countryCode : destination phone country code
     * @param phone       : destination phone
     */
    @ApiAction(desc = "send an sms", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun send(message: String, countryCode: String, phone: String): ResultMsg<Boolean> {
        return this.svc.send(message, countryCode, phone)
    }


    /**
     * sends a message using the template and variables supplied
     * @param name        : name of the template
     * @param countryCode : destination phone country code
     * @param phone       : destination phone
     * @param vars        : values to replace the variables in template ( extra args on command line
     *                      will be automatically added into this collection )
     */
    @ApiAction(desc = "send an sms using a template", roles = "@parent", verb = "@parent", protocol = "cli")
    fun sendUsingTemplate(name: String, countryCode: String, phone: String, vars: Vars): ResultMsg<Boolean> {
        return this.svc.sendUsingTemplate(name, countryCode, phone, vars)
    }
}
