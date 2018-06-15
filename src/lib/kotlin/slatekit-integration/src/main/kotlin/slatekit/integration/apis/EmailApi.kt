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
import slatekit.common.ResultMsg
import slatekit.common.Vars

@Api(area = "cloud", name = "email", desc = "api to send emails",
        auth = AuthModes.apiKey, roles = "ops", verb = Verbs.auto, protocol = Protocols.all)
class EmailApi(val svc: slatekit.core.email.EmailService, override val context: slatekit.core.common.AppContext) : ApiWithSupport {

    /**
     * Sends the email message
     * @param to      : The destination email address
     * @param subject : The subject of email
     * @param body    : The body of the email
     * @param html    : Whether or not the email is html formatted
     * @return
     */
    @ApiAction(desc = "send an email")
    fun send(to: String, subject: String, body: String, html: Boolean): ResultMsg<Boolean> {
        val content = slatekit.common.Uris.readText("user://blendlife-kotlin/templates/template_vacation.html")!!
        return this.svc.send(to, subject, content, html)
    }


    /**
     * Sends the email message
     * @param to      : The destination email address
     * @param subject : The subject of email
     * @param body    : The body of the email
     * @param html    : Whether or not the email is html formatted
     * @return
     */
    @ApiAction(desc = "send an email")
    fun sendFile(to: String, subject: String, filePath: String, html: Boolean): ResultMsg<Boolean> {
        val content = slatekit.common.Uris.readText(filePath)!!
        return this.svc.send(to, subject, content, html)
    }


    /**
     * sends a message using the template and variables supplied
     * @param name    : name of the template
     * @param to      : The destination email address
     * @param subject : The subject of email
     * @param html    : Whether or not the email is html formatted
     * @param vars    : values to replace the variables in template ( extra args on command line
     *                      will be automatically added into this collection )
     */
    @ApiAction(desc = "send an email using a template")
    fun sendUsingTemplate(name: String, to: String, subject: String, html: Boolean, vars: Vars): ResultMsg<Boolean> {
        return this.svc.sendUsingTemplate(name, to, subject, html, vars)
    }
}