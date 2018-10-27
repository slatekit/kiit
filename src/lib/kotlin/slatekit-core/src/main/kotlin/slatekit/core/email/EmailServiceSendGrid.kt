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

import slatekit.common.security.ApiLogin
import slatekit.common.Failure
import slatekit.common.ResultMsg
import slatekit.common.http.HttpConstants
import slatekit.common.http.HttpCredentials
import slatekit.common.http.HttpMethod
import slatekit.common.http.HttpRequest
import slatekit.common.results.ResultFuncs
import slatekit.common.templates.Templates

class EmailServiceSendGrid(
    user: String,
    key: String,
    phone: String,
    templates: Templates? = null,
    sender: ((HttpRequest) -> ResultMsg<Boolean>)? = null
)
    : EmailService(templates) {
    val _sender = sender

    val _settings = EmailSettings(user, key, phone)
    private val _baseUrl = "https://api.sendgrid.com/api/mail.send.json"

    /**
     * Initialize with api credentials
     * @param apiKey
     */
    constructor(apiKey: ApiLogin, templates: Templates? = null) :
            this(apiKey.key, apiKey.pass, apiKey.account, templates)

    override fun send(msg: EmailMessage): ResultMsg<Boolean> {

        // Parameters
        val bodyArg = if (msg.html) "html" else "text"

        // Create an immutable http request.
        val req = HttpRequest(
                url = _baseUrl,
                method = HttpMethod.POST,
                params = listOf(
                        Pair("api_user", _settings.user),
                        Pair("api_key", _settings.key),
                        Pair("to", msg.to),
                        Pair("from", _settings.account),
                        Pair("subject", msg.subject),
                        Pair(bodyArg, msg.body)
                ),
                headers = null,
                credentials = HttpCredentials("Basic", _settings.user, _settings.key),
                entity = null,
                connectTimeOut = HttpConstants.defaultConnectTimeOut,
                readTimeOut = HttpConstants.defaultReadTimeOut
        )

        // This optionally uses the IO monad supplied or actually posts ( impure )
        // This approach allows for testing this without actually sending a http request.
        return _sender?.let { s -> s(req) } ?: post(req)
    }

    private fun post(req: HttpRequest): ResultMsg<Boolean> {
        val res = slatekit.common.http.HttpClient.post(req)
        return if (res.is2xx) ResultFuncs.success(true, msg = res.result?.toString() ?: "") else Failure("error sending sms to ${req.url}")
    }
}
