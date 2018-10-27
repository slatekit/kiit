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

package slatekit.core.email

import slatekit.common.*
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.common.templates.Templates

abstract class EmailService(val templates: Templates? = null) {

    /**
     * Sends the email message
     * @param msg
     * @return
     */
    abstract fun send(msg: EmailMessage): ResultMsg<Boolean>

    /**
     * Sends the email message
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param body : The body of the email
     * @param html : Whether or not the email is html formatted
     * @return
     */
    fun send(to: String, subject: String, body: String, html: Boolean): ResultMsg<Boolean> {
        // NOTE: This guards are more readable that other alternatives
        val result = validate(to, subject)
        return if (result.success) {
            send(EmailMessage(to, subject, body, html))
        } else {
            Failure(result.msg)
        }
    }

    /**
     * sends a message using the template and variables supplied
     * @param to : The destination email address
     * @param subject : The subject of email
     * @param html : Whether or not the email is html formatted
     * @param variables : values to replace the variables in template
     */
    fun sendUsingTemplate(name: String, to: String, subject: String, html: Boolean, variables: Vars): ResultMsg<Boolean> {
        val result = validate(to, subject)
        return if (result.success) {
            // Send the message
            // send(to, subject, message, html)
            templates?.let { t ->
                val result = t.resolveTemplateWithVars(name, variables.toMap())
                val message = result
                send(EmailMessage(to, subject, message ?: "", html))
            } ?: Failure("templates are not setup")
        } else {
            Failure(result.msg)
        }
    }

    private fun validate(to: String, subject: String): ResultMsg<String> =
            if (to.isNullOrEmpty()) failure(msg = "to not provided")
            else if (subject.isNullOrEmpty()) failure(msg = "subject not provided")
            else success("")
}
