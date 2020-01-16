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

import okhttp3.Request
import slatekit.common.info.ApiLogin
import slatekit.common.templates.Templates
import slatekit.http.HttpRPC
import slatekit.results.*
import slatekit.results.builders.Outcomes

class SendGrid(
    user: String,
    key: String,
    phone: String,
    templates: Templates? = null
) :
    EmailService(templates) {

    val settings = EmailSettings(user, key, phone)
    private val baseUrl = "https://api.sendgrid.com/api/mail.send.json"

    /**
     * Initialize with api credentials
     * @param apiKey
     */
    constructor(apiKey: ApiLogin, templates: Templates? = null) :
            this(apiKey.key, apiKey.pass, apiKey.account, templates)

    /**
     * Validates the model supplied
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    override fun validate(model: EmailMessage): Outcome<EmailMessage> {
        return if (model.to.isNullOrEmpty()) Outcomes.invalid("to not provided")
        else if (model.subject.isNullOrEmpty()) Outcomes.invalid("subject not provided")
        else Outcomes.success(model)
    }

    /**
     * Builds the HttpRequest for the model
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    override fun build(model: EmailMessage): Outcome<Request> {
        // Parameters
        val bodyArg = if (model.html) "html" else "text"
        val request = HttpRPC().build(
                method = HttpRPC.Method.Post,
                urlRaw = baseUrl,
                headerParams = null,
                creds = HttpRPC.Auth.Basic(settings.user, settings.key),
                body = HttpRPC.Body.FormData(listOf(
                        "api_user" to settings.user,
                        "api_key"  to settings.key,
                        "to"       to model.to,
                        "from"     to settings.account,
                        "subject"  to model.subject,
                        bodyArg    to model.body
                )
        ))
        return Success(request)
    }
}
