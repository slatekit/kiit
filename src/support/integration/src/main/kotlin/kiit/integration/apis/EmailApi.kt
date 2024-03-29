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
import kiit.common.io.Uris
import kiit.common.values.Vars
import kiit.common.crypto.Encryptor
import kiit.common.log.Logger
import kiit.comms.email.EmailService
import kiit.results.Outcome

@Api(area = "cloud", name = "email", desc = "api to send emails",
        auth = AuthModes.KEYED, roles = ["ops"], verb = Verbs.AUTO, sources = [Sources.ALL])
class EmailApi(val svc: EmailService, override val context: Context) : FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    /**
     * Sends the email message
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param body : The body of the email
     * @param html : Whether or not the email is html formatted
     * @return
     */
    @Action(desc = "send an email")
    suspend fun send(to: String, subject: String, body: String, html: Boolean): Outcome<Boolean> {
        return this.svc.send(to, subject, body, html).map { true }
    }

    /**
     * Sends the email message
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param body : The body of the email
     * @param html : Whether or not the email is html formatted
     * @return
     */
    @Action(desc = "send an email")
    suspend fun sendFile(to: String, subject: String, filePath: String, html: Boolean): Outcome<Boolean> {
        val content = Uris.readText(filePath)
        return this.svc.send(to, subject, content ?: "", html).map { true }
    }

    /**
     * sends a message using the template and variables supplied
     * @param name : name of the template
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param html : Whether or not the email is html formatted
     * @param vars : values to replace the variables in template ( extra args on command line
     *                      will be automatically added into this collection )
     */
    @Action(desc = "send an email using a template")
    suspend fun sendUsingTemplate(name: String, to: String, subject: String, html: Boolean, vars: Vars): Outcome<Boolean> {
        return this.svc.sendTemplate(name, to, subject, html, vars).map { true }
    }
}