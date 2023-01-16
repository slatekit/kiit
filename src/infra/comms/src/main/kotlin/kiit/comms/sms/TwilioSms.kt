/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.comms.sms

import okhttp3.Request
import kiit.common.info.ApiLogin
import kiit.utils.templates.Templates
import kiit.common.types.Countries
import kiit.common.types.Country
import kiit.http.HttpRPC
import kiit.results.*
import kiit.results.builders.Outcomes

/**
 * simple service to send sms messages using Twilio with support for templates and
 * countries
 *
 * @param key : The twilio sid / account
 * @param password : The twilio password
 * @param phone : The twilio phone number
 * @param templates: The templates supported ( See templates in utils for more info )
 * @param ctns : The countries supported
 * @note:
 *
curl -X POST 'https://api.twilio.com/2010-04-01/Accounts/BCa1234567890d49dcffd51736e0e2e123/Messages.json' \
--data-urlencode 'To=3475143333'  \
--data-urlencode 'From=+17181234567'  \
--data-urlencode 'Body=test from slate sms service' \
-u ACb1234567890d49dcffd51736e0e2e123:xyz5a123456d78d415eaab7ab90e4bab
 */
class TwilioSms(
        key: String,
        password: String,
        phone: String,
        templates: Templates? = null,
        countries: List<Country> = listOf(Countries.usa),
        client: HttpRPC = HttpRPC()
) :
    SmsService(templates, countries, client) {
    private val settings = SmsSettings(key, password, phone)
    private val baseUrl = "https://api.twilio.com/2010-04-01/Accounts/$key/Messages.json"

    /**
     * Initialize with api credentials
     *
     * @param apiKey
     */
    constructor(apiKey: ApiLogin, templates: Templates? = null, countries: List<Country>? = null) :
            this(apiKey.key, apiKey.pass, apiKey.account, templates, countries ?: listOf(kiit.common.types.Countries.usa))

    /**
     * Validates the model supplied
     * @param model: The data model to send ( e.g. EmailMessage )
     */
    override fun validate(model: SmsMessage): Outcome<SmsMessage> {
        return when {
            model.countryCode.isNullOrEmpty() -> Outcomes.invalid("country code not provided")
            model.phone.isNullOrEmpty() -> Outcomes.invalid("phone not provided")
            else -> Outcomes.success(model)
        }
    }

    /**
     * sends the sms message to the phone
     *
     * @param model : message to send
     * @return
     */
    override fun build(model: SmsMessage): Outcome<Request> {

        val phoneResult = massagePhone(model.countryCode, model.phone)
        return when (phoneResult) {
            is Success -> {
                val phone = phoneResult.value
                val request = HttpRPC().build(
                        verb = HttpRPC.Method.Post,
                        url  = baseUrl,
                        meta = null,
                        auth = HttpRPC.Auth.Basic(settings.key, settings.password),
                        body = HttpRPC.Body.FormData(listOf(
                                Pair("To", phone),
                                Pair("From", settings.account),
                                Pair("Body", model.msg))
                        )
                )
                Success(request)
            }
            is Failure -> Outcomes.errored(phoneResult.desc)
        }
    }

    /**
     * Format phone to ensure "+" and "iso" is present. e.g. "+{iso}{phone}"
     * @param iso
     * @param phone
     * @return
     */
    override fun massagePhone(iso: String, phone: String): Notice<String> {
        // Remove the "+" and allow base function to ensure the country code is present
        val result = super.massagePhone(iso, phone.replace("+", ""))
        return when (result) {
            is Success -> Success("+" + result.value)
            is Failure -> result
        }
    }
}
