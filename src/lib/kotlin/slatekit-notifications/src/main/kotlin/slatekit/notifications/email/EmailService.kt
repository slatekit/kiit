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
import slatekit.common.Vars
import slatekit.notifications.common.Sender
import slatekit.results.*
import slatekit.results.builders.Outcomes

abstract class EmailService(val templates: Templates? = null) : Sender<EmailMessage> {

    /**
     * Sends the email message
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param body : The body of the email
     * @param html : Whether or not the email is html formatted
     * @return
     */
    fun send(to: String, subject: String, body: String, html: Boolean): Outcome<String> {
        // NOTE: This guards are more readable that other alternatives
        val validationResult = validate(to, subject)
        val result = validationResult.then {
            sendSync(EmailMessage(to, subject, body, html))
        }.toOutcome()
        return result
    }

    /**
     * sends a message using the template and variables supplied
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param html : Whether or not the email is html formatted
     * @param variables : values to replace the variables in template
     */
    fun sendUsingTemplate(name: String, to: String, subject: String, html: Boolean, variables: Vars): Outcome<String> {
        val result = validate(to, subject)
        return if (result.success) {
            // Send the message
            // send(to, subject, message, html)
            templates?.let { t ->
                val result = t.resolveTemplateWithVars(name, variables.toMap())
                val message = result
                sendSync(EmailMessage(to, subject, message ?: "", html))
            } ?: Outcomes.invalid("templates are not setup")
        } else {
            Outcomes.errored(result.msg)
        }
    }

    private fun validate(to: String, subject: String): Notice<String> =
            when {
                to.isNullOrEmpty() -> Failure("to not provided")
                subject.isNullOrEmpty() -> Failure("subject not provided")
                else -> Success("")
            }
}
