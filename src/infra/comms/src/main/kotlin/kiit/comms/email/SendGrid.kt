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
import slatekit.utils.templates.Templates
import slatekit.http.HttpRPC
import slatekit.results.*
import slatekit.results.builders.Outcomes

import org.json.simple.JSONArray
import org.json.simple.JSONObject

class SendGrid(
        user: String,
        key: String,
        phone: String,
        templates: Templates? = null,
        client: HttpRPC = HttpRPC()
) :
    EmailService(templates, client) {

    val settings = EmailSettings(user, key, phone)
    //private val baseUrlOld = "https://api.sendgrid.com/api/mail.send.json"
    private val baseUrl = "https://api.sendgrid.com/v3/mail/send"

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
     *
     * {
     *       "personalizations": [
     *           {
     *               "to": [
     *                   {
     *                       "email": "jl@gmail.com",
     *                       "name": "justice league"
     *                   }
     *               ]
     *           }
     *       ],
     *       "from": {
     *           "email": "support@slatekit.com",
     *           "name": "Slate.Kit"
     *       },
     *       "subject": "Hello, World from Postman!",
     *       "content": [
     *           {
     *               "type": "text/html",
     *               "value": "<strong>Testing</strong> hello world from <span style='color:Red'>Postman</span>"
     *           }
     *       ]
     *   }
     */
    override fun build(model: EmailMessage): Outcome<Request> {
        // Parameters
        val contentType = if (model.html) "text/html" else "text/plain"
        val root = JSONObject()

        // To
        val to = JSONObject()
        to["email"] = model.to
        to["name"] = ""

        // From
        val from = JSONObject()
        from["email"] = settings.account
        from["name"] = settings.user

        // Content
        val contentArray = JSONArray()
        val content = JSONObject()
        content["type"] = contentType
        content["value"] = model.body
        contentArray.add(content)

        // Personalizations
        val pers = JSONArray()
        val personalTo = JSONObject()
        val tos = JSONArray()
        tos.add(to)
        personalTo["to"] = tos
        pers.add(personalTo)

        // Full object
        root["subject"] = model.subject
        root["from"] = from
        root["content"] = contentArray
        root["personalizations"] = pers

        val json = root.toJSONString()
        val request = HttpRPC().build(
                url  = baseUrl,
                verb = HttpRPC.Method.Post,
                meta = mapOf(
                    "Authorization" to "Bearer ${settings.key}",
                    "Content-Type" to "application/json"
                ),
                body = HttpRPC.Body.JsonContent(json)
        )
        return Success(request)
    }
}
