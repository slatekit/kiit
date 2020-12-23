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

package slatekit.notifications.email

import slatekit.common.templates.Templates
import slatekit.common.types.Vars
import slatekit.notifications.common.TemplateSender
import slatekit.results.*

abstract class EmailService(override val templates: Templates? = null) : TemplateSender<EmailMessage> {

    /**
     * Sends the email message
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param body : The body of the email
     * @param html : Whether or not the email is html formatted
     * @return
     */
    suspend fun send(to: String, subject: String, body: String, html: Boolean): Outcome<String> {
        return send(EmailMessage(to, subject, body, html))
    }

    /**
     * sends a message using the template and variables supplied
     * @param template : The name of the template
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param html : Whether or not the email is html formatted
     * @param variables : values to replace the variables in template
     */
    suspend fun sendTemplate(template: String, to: String, subject: String, html: Boolean, variables: Vars): Outcome<String> {
        return sendTemplate(template, variables) { EmailMessage(to, subject, it, html) }
    }
}
