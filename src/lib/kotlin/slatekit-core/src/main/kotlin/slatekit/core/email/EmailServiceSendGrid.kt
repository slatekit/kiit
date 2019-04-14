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

import okhttp3.Request
import slatekit.common.*
import slatekit.common.info.ApiLogin
import slatekit.common.templates.Templates
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.then

class EmailServiceSendGrid(
    user: String,
    key: String,
    phone: String,
    templates: Templates? = null
)
    : EmailService(templates) {

    val settings = EmailSettings(user, key, phone)
    private val baseUrl = "https://api.sendgrid.com/api/mail.send.json"

    /**
     * Initialize with api credentials
     * @param apiKey
     */
    constructor(apiKey: ApiLogin, templates: Templates? = null) :
            this(apiKey.key, apiKey.pass, apiKey.account, templates)

    fun build(msg: EmailMessage): Notice<Request> {
        // Parameters
        val bodyArg = if (msg.html) "html" else "text"
        val request = HttpRPC().build(
                method = HttpRPC.Method.Post,
                urlRaw = baseUrl,
                headerParams = null,
                creds = HttpRPC.Auth.Basic(settings.user, settings.key),
                body = HttpRPC.Body.FormData(listOf(
                        Pair("api_user", settings.user),
                        Pair("api_key", settings.key),
                        Pair("to", msg.to),
                        Pair("from", settings.account),
                        Pair("subject", msg.subject),
                        Pair(bodyArg, msg.body)
                )
        ))
        return Success(request)
    }

    override fun send(msg: EmailMessage): Notice<Boolean> {
        return build(msg).then {
            val response = HttpRPC().call(it)
            val result = response.fold({ Success(true) }, { Failure(it.message ?: "") })
            result
        }
    }
}
